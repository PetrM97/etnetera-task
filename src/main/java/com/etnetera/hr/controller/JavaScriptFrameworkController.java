package com.etnetera.hr.controller;

import com.etnetera.hr.rest.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;

import javax.validation.Valid;

/**
 * Simple REST controller for accessing application logic.
 * 
 * @author Etnetera
 *
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

	@PostMapping("/frameworks")
	public JavaScriptFramework frameworkAdd(@RequestBody @Valid JavaScriptFramework framework) throws ValidationError {
		if(framework.getId() != null && repository.existsById(framework.getId())) throw new ValidationError("id", "AlreadyExists");
		repository.save(framework);
		return framework;
	}

	@DeleteMapping("/frameworks/{idString}")
	public JavaScriptFramework frameworkDelete(@PathVariable String idString) throws ValidationError {
		long id;
		try {
			id = Long.parseLong(idString);
		}catch (NumberFormatException ex){
			throw new ValidationError("id", "NotANumber");
		}
		JavaScriptFramework framework = repository.findById(id).orElseThrow(() -> new ValidationError("id", "DoesNotExist"));
		repository.delete(framework);
		return framework;
	}

}
