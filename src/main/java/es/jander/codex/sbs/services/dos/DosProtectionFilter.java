package es.jander.codex.sbs.services.dos;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@ConditionalOnProperty(name = DosProtectionFilter.DOS_PROTECTION_ENABLER_PROPERTY_NAME, havingValue = "true")
public class DosProtectionFilter extends GenericFilterBean
{
    public static final String DOS_PROTECTION_ENABLER_PROPERTY_NAME = "app.dos.enabled";

    private final int bucketCapacity;
    private final List<String> paths;
    private final AtomicInteger bucket;
    private final AntPathMatcher matcher;

    @Autowired
    public DosProtectionFilter(DosProperties dosProperties)
    {
        log.info("Bucket size is: {}, paths: {}", dosProperties.getBucketCapacity(), dosProperties.getPaths());
        this.bucketCapacity = dosProperties.getBucketCapacity();
        bucket = new AtomicInteger(bucketCapacity);
        this.paths = dosProperties.getPaths();
        this.matcher = new AntPathMatcher();
    }

    @Scheduled(fixedRateString = "${app.dos.tokenSpawnTimeInMillis:100}")
    public void reportCurrentTime()
    {
        if (bucket.get() < bucketCapacity) {
            bucket.incrementAndGet();
        }
    }

    public synchronized boolean isRequestAcceptable()
    {
        if (bucket.get() == 0) {
            return false;
        } else {
            bucket.decrementAndGet();
            return true;
        }
    }

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException
    {
        if (paths == null || paths.isEmpty()) {
            log.info("No paths protected");
            filterChain.doFilter(servletRequest, servletResponse);
        }
        else if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            String requestURI = request.getRequestURI();
            log.info("Is a match?: '{}' to '{}' = {}", paths.get(0), requestURI, matcher.match(paths.get(0), requestURI));
            boolean isProtectedPath = paths.parallelStream().anyMatch(pattern -> matcher.match(pattern, requestURI));
            log.info("Is '{}' Protected? {}", requestURI, isProtectedPath);
            if (!isProtectedPath || isRequestAcceptable()) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                ((HttpServletResponse) servletResponse).sendError(HttpStatus.TOO_MANY_REQUESTS.value());
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}