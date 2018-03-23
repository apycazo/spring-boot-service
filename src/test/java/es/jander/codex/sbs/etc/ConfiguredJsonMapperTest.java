package es.jander.codex.sbs.etc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RunWith(SpringRunner.class)
@Import(ConfiguredJsonMapper.class)
public class ConfiguredJsonMapperTest
{
    @Autowired
    @Qualifier(ConfiguredJsonMapper.BEAN_NAME)
    private ObjectMapper jsonMapper;

    @Test
    public void test_deserialize_with_single_quotes () throws IOException
    {
        String simpleObjectAsString = "{ 'id': 1, 'value': 'example'}";
        SimpleObject simpleObject = jsonMapper.readValue(simpleObjectAsString, SimpleObject.class);

        assertThat(simpleObject).isNotNull();
        assertThat(simpleObject.getId()).isEqualTo(1);
        assertThat(simpleObject.getValue()).isEqualTo("example");
    }
}