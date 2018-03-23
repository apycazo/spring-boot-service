package es.jander.codex.sbs.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo
{
    @Id
    @NotEmpty
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    @NotEmpty
    @Length(min = 3, max = 64)
    private String name;

    @NotEmpty
    @Length(min = 6, max = 20)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    @JsonSerialize(using = ToStringSerializer.class)
    private long created = Instant.now().toEpochMilli();

    @Builder.Default
    @JsonSerialize(using = ToStringSerializer.class)
    private long updated = Instant.now().toEpochMilli();

}