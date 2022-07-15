package com.etnetera.hr;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Class used for Spring Boot/MVC based tests.
 *
 * @author Etnetera
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JavaScriptFrameworkTests {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private JavaScriptFrameworkRepository repository;

    @Before
    public void before() {
        repository.deleteAll();
    }

    private void prepareData() {
        // Start with clean repository - the order of test cases can be arbitrary
        repository.deleteAll();

        JavaScriptFramework react = new JavaScriptFramework("ReactJS");
        JavaScriptFramework vue = new JavaScriptFramework("Vue.js");

        repository.save(react);
        repository.save(vue);
    }

    @Test
    public void frameworksTest() throws Exception {
        prepareData();

        mockMvc.perform(get("/frameworks")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                //.andExpect(jsonPath("$[0].id", is(1))) // IDs commented out as the repository may have already been used in other test cases
                .andExpect(jsonPath("$[0].name", is("ReactJS")))
                //.andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Vue.js")));
    }

    @Test
    public void addFrameworkValid() throws Exception {
        JavaScriptFramework framework = new JavaScriptFramework("Svelte");
        mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
                .andExpect(status().isOk()) // Insert returns the newly created record
                .andExpect(jsonPath("$.name", is(framework.getName())))
                .andExpect(jsonPath("$.id", Matchers.anything()));
    }

    @Test
    public void addFrameworkAllProps() throws Exception {
        JavaScriptFramework framework = new JavaScriptFramework("Vanilla.js");
        framework.setVersion("1.0.0-dev");
        framework.setDeprecationDate(Date.valueOf(LocalDate.MAX));
        framework.setHypeLevel(10);
        mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
                .andExpect(status().isOk()) // Insert returns the newly created record
                .andExpect(jsonPath("$.name", is(framework.getName())))
                .andExpect(jsonPath("$.id", Matchers.anything()))
                .andExpect(jsonPath("$.hypeLevel", is(framework.getHypeLevel())))
                .andExpect(jsonPath("$.deprecationDate", is(framework.getDeprecationDate().toString())));
    }

    @Test
    public void addFrameworkInvalidVersion() throws Exception {
        JavaScriptFramework framework = new JavaScriptFramework("Vanilla.js");
        framework.setVersion("1.0.0.1");
        mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
                .andExpect(status().isBadRequest()) // Insert returns the newly created record
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("version")))
                .andExpect(jsonPath("$.errors[0].message", is("Pattern")));
    }

    @Test
    public void addFrameworkInvalidHypeLevel() throws Exception {
        JavaScriptFramework framework = new JavaScriptFramework("Vanilla.js");
        framework.setHypeLevel(11);
        mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
                .andExpect(status().isBadRequest()) // Insert returns the newly created record
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("hypeLevel")))
                .andExpect(jsonPath("$.errors[0].message", is("Range")));
    }

	/*
		If the date cannot be parsed (i.e. is invalid), it is set to be null.
		Therefore, there is no unit test for an invalid date.
	 */

    @Test
    public void addFrameworkInvalidJson() throws Exception {
        mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content("{\"name\": neverGonna}"))
                .andExpect(status().isBadRequest()) // Insert returns the newly created record
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("")))
                .andExpect(jsonPath("$.errors[0].message", is("JsonParseException")));
    }

    @Test
    public void addFrameworkNull() throws Exception {
        JavaScriptFramework framework = new JavaScriptFramework();
        mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("name")))
                .andExpect(jsonPath("$.errors[0].message", is("NotEmpty")));
    }

    @Test
    public void addFrameworkLongName() throws Exception {
        JavaScriptFramework framework = new JavaScriptFramework("verylongnameofthejavascriptframeworkjavaisthebest");
        mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("name")))
                .andExpect(jsonPath("$.errors[0].message", is("Size")));
    }

    @Test
    public void getFramework() throws Exception {
        prepareData();
        JavaScriptFramework framework = repository.findAll().iterator().next();
        // ID may not be 1 due to other tests
        mockMvc.perform(get("/frameworks/" + framework.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(framework.getId().intValue())))
                .andExpect(jsonPath("$.name", is(framework.getName())));
    }

    @Test
    public void getFrameworkInvalid() throws Exception {
        mockMvc.perform(get("/frameworks/1000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("id")))
                .andExpect(jsonPath("$.errors[0].message", is("NotFound")));
    }

    @Test
    public void addFrameworkDuplicate() throws Exception {
        // Save the framework and then try to add it again with the same ID
        JavaScriptFramework framework = new JavaScriptFramework("validName");
        framework.setId(1L);
        repository.save(framework);
        // ID may not be 1 due to other tests
        framework = repository.findAll().iterator().next();
        mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("id")))
                .andExpect(jsonPath("$.errors[0].message", is("AlreadyExists")));
    }

    @Test
    public void modifyFramework() throws Exception {
        prepareData();
        JavaScriptFramework framework = repository.findAll().iterator().next();
        // ID may not be 1 due to other tests
        framework.setName("Nothing");
        framework.setVersion("2.1.0-dev");
        framework.setDeprecationDate(Date.valueOf(LocalDate.now()));
        framework.setHypeLevel(9);
        // By modifying a framework, the number of records must remain the same
        long initialCount = repository.count();
        mockMvc.perform(post("/frameworks/" + framework.getId()).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(framework.getName())))
                .andExpect(jsonPath("$.version", is(framework.getVersion())))
                .andExpect(jsonPath("$.hypeLevel", is(framework.getHypeLevel())))
                .andExpect(jsonPath("$.deprecationDate", is(framework.getDeprecationDate().toString())));
        // ID of the framework is not checked as it is assigned by the database
        assert initialCount == repository.count();
    }

    @Test
    public void deleteInvalid() throws Exception {
        mockMvc.perform(delete("/frameworks/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("id")))
                .andExpect(jsonPath("$.errors[0].message", is("NotFound")));

        mockMvc.perform(delete("/frameworks/notanumber"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].field", is("id")))
                .andExpect(jsonPath("$.errors[0].message", is("NotANumber")));
    }

    @Test
    public void deleteValid() throws Exception {
        JavaScriptFramework framework = new JavaScriptFramework("Preact");
        framework.setId(10L);
        repository.save(framework);
        framework = repository.findAll().iterator().next();
        assert repository.existsById(framework.getId());
        mockMvc.perform(delete("/frameworks/" + framework.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(framework.getName())));
    }

}
