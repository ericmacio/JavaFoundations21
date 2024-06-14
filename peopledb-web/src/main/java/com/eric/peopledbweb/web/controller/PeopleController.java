package com.eric.peopledbweb.web.controller;

import com.eric.peopledbweb.business.model.Person;
import com.eric.peopledbweb.business.service.PersonService;
import com.eric.peopledbweb.exception.StorageException;
import com.eric.peopledbweb.data.FileStorageRepository;
import com.eric.peopledbweb.data.PersonRepository;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;

@Controller
@RequestMapping("/people")
@Log4j2
public class PeopleController {

    private PersonRepository personRepository;
    private FileStorageRepository fileStorageRepository;
    private PersonService personService;

    public PeopleController(PersonService personService,
                            PersonRepository personRepository,
                            FileStorageRepository fileStorageRepository) {
        this.personService = personService;
        this.personRepository = personRepository;
        this.fileStorageRepository = fileStorageRepository;
    }

    @ModelAttribute("people")
    public Page<Person> getPeople(@PageableDefault(size = 10) Pageable page) {
        return personService.findAll(page);
    }

    @ModelAttribute("person")
    public Person getPerson() {
        Person person = new Person();
        return person;
    }

    @GetMapping
    public String showPeoplePage() {
        return "people";
    }

    @GetMapping("/images/{resource}")
    public ResponseEntity<Resource> getResource(@PathVariable String resource) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, format("attachement; filename=%s", resource))
                .body(fileStorageRepository.findByName(resource));
    }

    @PostMapping
    public String savePerson(Model model, @Valid Person person, Errors errors, @RequestParam MultipartFile photoFile) throws IOException {
        log.info("Save person: " + person);
        log.info(photoFile.getOriginalFilename());
        log.info("File size: " + photoFile.getSize());
        if(photoFile.getSize() == 0) person.setPhotoFile(null);
        log.info("Errors: " + errors);
        if(!errors.hasErrors()) {
            try {
                personService.save(person, photoFile.getInputStream());
                return "redirect:people";
            } catch (StorageException e) {
                model.addAttribute("errorMsg", "System is currently out of service. Upload of photo is not possible");
                return "people";
            }
        }
        return "people";
    }

    @PostMapping(params = "action=delete")
    public String deletePeople(@RequestParam Optional<List<Long>> select) {
        log.info("Delete");
        log.info(select);
        select.ifPresent(longs -> personService.deleteAllById(longs));
        return "redirect:people";
    }

    @PostMapping(params = "action=edit")
    public String updatePeople(@RequestParam Optional<List<Long>> select, Model model) {
        log.info("Update");
        log.info(select);
        if(select.isPresent()) {
            Optional<Person> person = personRepository.findById(select.get().get(0));
            log.info("Person: " + person);
            model.addAttribute("person", person);
        }
        return "people";
    }

    @PostMapping(params = "action=import")
    public String importCSV(@RequestParam("csvFile") MultipartFile csvFile) {
        log.info("CSV file name: " + csvFile.getOriginalFilename());
        log.info("CSV file size: " + csvFile.getSize());
        try {
            personService.importCSV(csvFile.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:people";
    }
}
