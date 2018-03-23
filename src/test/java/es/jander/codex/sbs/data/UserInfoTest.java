package es.jander.codex.sbs.data;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RunWith(SpringRunner.class)
public class UserInfoTest
{
    private @Autowired JacksonTester<UserInfo> json;

    @Test
    public void test_build_defaults ()
    {
        UserInfo userInfo = UserInfo.builder()
                .id(UUID.randomUUID().toString())
                .name("test user")
                .username("t.user")
                .build();

        assertThat(userInfo.isEnabled()).isTrue();
        assertThat(userInfo.getCreated()).isNotZero();
        assertThat(userInfo.getUpdated()).isNotZero();
    }

    @Test
    public void test_serialize () throws IOException
    {
        UserInfo userInfo = UserInfo.builder()
                .id(UUID.randomUUID().toString())
                .name("test user")
                .username("t.user")
                .build();

        JsonContent<UserInfo> str = json.write(userInfo);

        assertThat(str).hasJsonPathStringValue("name");
        assertThat(str).extractingJsonPathStringValue("name").isEqualTo(userInfo.getName());

        assertThat(str).hasJsonPathStringValue("username");
        assertThat(str).extractingJsonPathStringValue("username").isEqualTo(userInfo.getUsername());

        assertThat(str).hasJsonPathStringValue("id");
        assertThat(str).extractingJsonPathStringValue("id").isEqualTo(userInfo.getId());

        assertThat(str).hasJsonPathStringValue("created");
        assertThat(str)
                .extractingJsonPathStringValue("created")
                .isEqualTo(String.valueOf(userInfo.getCreated()));

        assertThat(str).hasJsonPathStringValue("updated");
        assertThat(str)
                .extractingJsonPathStringValue("updated")
                .isEqualTo(String.valueOf(userInfo.getUpdated()));

        assertThat(str).doesNotHaveJsonPathValue("password");
    }

    @Test
    public void test_deserialize_without_password () throws IOException
    {
        String original = "{\n" +
                "  \"id\" : \"c507e887-8f41-4367-b2f3-c2f8c8a7c15f\",\n" +
                "  \"name\" : \"test user\",\n" +
                "  \"username\" : \"t.user\",\n" +
                "  \"enabled\" : true,\n" +
                "  \"created\" : \"1521819444558\",\n" +
                "  \"updated\" : \"1521819444558\"\n" +
                "}";

        UserInfo userInfo = json.parseObject(original);
        assertThat(userInfo.getName()).isEqualTo("test user");
        assertThat(userInfo.getUsername()).isEqualTo("t.user");
        assertThat(userInfo.isEnabled()).isTrue();
        assertThat(userInfo.getCreated()).isEqualTo(1521819444558L);
        assertThat(userInfo.getUpdated()).isEqualTo(1521819444558L);

        assertThat(userInfo.getPassword()).isNullOrEmpty();
    }

    @Test
    public void test_deserialize_with_password () throws IOException
    {
        String original = "{\n" +
                "  \"id\" : \"c507e887-8f41-4367-b2f3-c2f8c8a7c15f\",\n" +
                "  \"name\" : \"test user\",\n" +
                "  \"username\" : \"t.user\",\n" +
                "  \"enabled\" : true,\n" +
                "  \"password\" : \"secret\",\n" +
                "  \"created\" : \"1521819444558\",\n" +
                "  \"updated\" : \"1521819444558\"\n" +
                "}";

        UserInfo userInfo = json.parseObject(original);

        assertThat(userInfo.getPassword()).isEqualTo("secret");
    }

}