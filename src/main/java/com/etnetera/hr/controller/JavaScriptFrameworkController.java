package com.etnetera.hr.controller;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import com.etnetera.hr.rest.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Simple REST controller for accessing application logic.
 *
 * @author Etnetera
 */
@RestController
public class JavaScriptFrameworkController extends EtnRestController {

    private final JavaScriptFrameworkRepository repository;

    @Autowired
    public JavaScriptFrameworkController(JavaScriptFrameworkRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/frameworks")
    public Iterable<JavaScriptFramework> frameworks() {
        return repository.findAll();
    }

    @GetMapping("/frameworks/{idString}")
    public JavaScriptFramework frameworks(@PathVariable String idString) throws ValidationError {
        long id = stringToIdOrThrow(idString);
        return repository.findById(id).orElseThrow(() -> new ValidationError("id", "NotFound"));
    }

    @PostMapping("/frameworks")
    public JavaScriptFramework frameworkAdd(@RequestBody @Valid JavaScriptFramework framework) throws ValidationError {
        if (framework.getId() != null && repository.existsById(framework.getId()))
            throw new ValidationError("id", "AlreadyExists");
        repository.save(framework);
        return framework;
    }

    @PostMapping("/frameworks/{idString}")
    public JavaScriptFramework frameworkUpdate(@RequestBody @Valid JavaScriptFramework framework, @PathVariable String idString) throws ValidationError {
        long id = stringToIdOrThrow(idString);
        if (!repository.existsById(id)) throw new ValidationError("id", "NotFound");
        JavaScriptFramework frameworkRepo = repository.findById(id).get();
        frameworkRepo.setName(framework.getName());
        frameworkRepo.setVersion(framework.getVersion());
        frameworkRepo.setHypeLevel(framework.getHypeLevel());
        frameworkRepo.setDeprecationDate(framework.getDeprecationDate());
        repository.save(frameworkRepo);
        return framework;
    }

    @DeleteMapping("/frameworks/{idString}")
    public JavaScriptFramework frameworkDelete(@PathVariable String idString) throws ValidationError {
        long id = stringToIdOrThrow(idString);
        JavaScriptFramework framework = repository.findById(id).orElseThrow(() -> new ValidationError("id", "NotFound"));
        repository.delete(framework);
        return framework;
    }


    private long stringToIdOrThrow(String idString) throws ValidationError {
        long id;
        try {
            id = Long.parseLong(idString);
        } catch (NumberFormatException ex) {
            throw new ValidationError("id", "NotANumber");
        }
        return id;
    }

}
