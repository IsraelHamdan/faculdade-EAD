package com.jdev.course.service;

import com.jdev.course.exceptions.CusmotomizeException.CourseErrorException;
import com.jdev.course.exceptions.CusmotomizeException.CourseNotFoundException;
import com.jdev.course.exceptions.CusmotomizeException.ModuleAlreadyExistsException;
import com.jdev.course.exceptions.CusmotomizeException.ModuleNotFoundException;
import com.jdev.course.model.Course;
import com.jdev.course.model.DTO.CourseUpdateDTO;
import com.jdev.course.model.DTO.ModuleCreateDTO;
import com.jdev.course.model.DTO.ModuleUpdateDTO;
import com.jdev.course.model.Module;
import com.jdev.course.model.materials.Material;
import com.jdev.course.repository.ModuleRepository;
import com.jdev.course.utils.GenerateRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private CourseService courseService;

    public List<Module> findAllModules() {
        return moduleRepository.findAll();
    }

    public List<Module> findAllActiveCourses() {
        return moduleRepository.findAllActiveCourses(true);
    }

    public Module create(ModuleCreateDTO moduleDTO) {
        if (moduleRepository.findByName(moduleDTO.name()).isEmpty()) {
            Module moduleForSave = this.modelingNewModuleForSave(moduleDTO);
            Module module = this.moduleRepository.save(moduleForSave);
            courseService.addModulesUnit(module.getId_course());
            return module;
        } else {
            throw new ModuleAlreadyExistsException();
        }
    }

    private Module modelingNewModuleForSave(ModuleCreateDTO moduleDTO) {
        Course course = courseService.findByCourseWithRegistration(moduleDTO.registrationCourse());
        if (course != null) {
            return Module.builder()
                    .name(moduleDTO.name())
                    .registration(GenerateRegister.newRegister())
                    .duration(0)
                    .quantityMaterials(0)
                    .teachers(Set.of())
                    .themes(List.of())
                    .supportMaterials(List.of())
                    .materials(List.of())
                    .id_course(course)
                    .active(true)
                    .build();
        } else {
            throw new CourseNotFoundException();
        }
    }

    public Module update(ModuleUpdateDTO updateDTO) {
        Optional<Module> module = moduleRepository.findByRegistration(updateDTO.registration());
        if (module.isPresent()) {
            if (updateDTO.name().isBlank() || updateDTO.name().isEmpty()) {
                throw new CourseErrorException("this name not valid!");
            } else {
                //update
                module.get().setName(updateDTO.name());
                return moduleRepository.save(module.get());
            }
        } else {
            throw new ModuleNotFoundException();
        }
    }

    public Module findByModuleWithName(String name) {
        Optional<Module> module = moduleRepository.findByName(name);
        return module.orElseThrow(ModuleNotFoundException::new);
    }

    public Module findByModuleWithRegistration(String registration) {
        Optional<Module> module = moduleRepository.findByRegistration(registration);
        return module.orElseThrow(ModuleNotFoundException::new);
    }

    public void setWithNotActive(String registration) {
        Module module = this.findByModuleWithRegistration(registration);
        module.setActive(false);
        moduleRepository.save(module);
    }

    public void addMaterialUnit(Module Module) {
        Module.setQuantityMaterials(Module.getQuantityMaterials() + 1);
        moduleRepository.save(Module);
    }
}
