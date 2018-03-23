package es.jander.codex.sbs.persistence;

import es.jander.codex.sbs.data.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserInfoRepositoryTest
{
    private @Autowired UserInfoRepository repository;

    @Before
    public void clearRepositoryBeforeTests ()
    {
        repository.deleteAll();
    }

    @Test
    public void test_create ()
    {
        UserInfo userInfo = UserInfo.builder().name("test-user-1").username("test-user-1").build();
        UserInfo persistedUserInfo = repository.save(userInfo);

        assertThat(persistedUserInfo.getId()).isEqualTo(userInfo.getId());
        assertThat(repository.count()).isEqualTo(1L);
    }

    @Test
    public void test_find_by_id ()
    {
        UserInfo userInfo = UserInfo.builder().name("test-user-1").username("test-user-1").build();
        repository.save(userInfo);

        UserInfo persistedUserInfo = repository.findOne(userInfo.getId());
        assertThat(persistedUserInfo.getName()).isEqualTo(persistedUserInfo.getName());
        assertThat(persistedUserInfo.getCreated()).isEqualTo(persistedUserInfo.getCreated());
    }

    @Test
    public void test_find_by_username ()
    {
        UserInfo userInfo = UserInfo.builder().name("test-user-1").username("test-user-1").build();
        repository.save(userInfo);

        UserInfo persistedUserInfo = repository.findByUsername(userInfo.getUsername());
        assertThat(persistedUserInfo).isNotNull();
        assertThat(persistedUserInfo.getName()).isEqualTo(persistedUserInfo.getName());
        assertThat(persistedUserInfo.getCreated()).isEqualTo(persistedUserInfo.getCreated());
    }

    @Test
    public void test_find_active_users ()
    {
        int userCountExpected = 10;
        // creates a bunch of users
        IntStream.range(0, userCountExpected).forEach(index -> {
            String userName = "test-user-" + index;
            boolean isActiveUser = index % 2 == 0;
            UserInfo user = UserInfo.builder().name(userName).username(userName).enabled(isActiveUser).build();
            repository.save(user);
        });

        assertThat(repository.count()).isEqualTo(userCountExpected);

        // find only active users
        List<UserInfo> activeUsers = repository.findAllUsersByStatus(true);
        assertThat(activeUsers).isNotEmpty();
        assertThat(activeUsers.size()).isEqualTo(userCountExpected / 2);
        activeUsers.forEach(user -> assertThat(user.isEnabled()).isTrue());

        // find only disabled users
        List<UserInfo> disabledUsers = repository.findAllUsersByStatus(false);
        assertThat(disabledUsers).isNotEmpty();
        assertThat(disabledUsers.size()).isEqualTo(userCountExpected / 2);
        disabledUsers.forEach(user -> assertThat(user.isEnabled()).isFalse());
    }
}