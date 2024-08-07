package com.example.Controller;

import com.example.exceptions.ValidationException;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;
import java.util.Set;

//@Hidden
@Slf4j
public abstract class GenericUpdateController<T, ID> {

    @Autowired
    private JpaRepository<T, ID> repository;

    private static final Set<String> IMMUTABLE_FIELDS = Set.of("id", "createdDate");
    private static final Set<String> FIELD_VALUES_FOR_PROCESSING = Set.of("description", "title");



    @PatchMapping("/{id}/{fieldName}")
    public ResponseEntity<?> updateField(@PathVariable ID id, @PathVariable String fieldName, @RequestBody Object newValue) {
        if (IMMUTABLE_FIELDS.contains(fieldName.toLowerCase())) {
            return ResponseEntity.badRequest().body(fieldName + " cannot be modified.");
        }



        Optional<T> optionalEntity = repository.findById(id);

        if (optionalEntity.isPresent()) {
            T entity = optionalEntity.get();
            if (FIELD_VALUES_FOR_PROCESSING.contains(fieldName.toString()) && newValue != null) {
                // Removes only the leading and trailing quotes
                newValue = newValue.toString().replaceAll("^\"+|\"+$", "");
                // Replace escaped quotes with actual quotes inside the string
                newValue = newValue.toString().replace("\\\"", "\"");
            }

//            try {
                if(FIELD_VALUES_FOR_PROCESSING.contains(fieldName.toString()) &&  newValue == null || newValue.toString().trim().isBlank() || newValue.toString().trim().isEmpty()) {
                    throw new IllegalArgumentException(fieldName + " cannot be null or empty");
                }
//            } catch (RuntimeException e) {
//                log.error(e.getMessage());
//            }

            try {
                String setterMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                if(FIELD_VALUES_FOR_PROCESSING.contains(fieldName.toString())) {
                    entity.getClass().getMethod(setterMethodName, newValue.getClass()).invoke(entity, newValue.toString());
                } else {
                    entity.getClass().getMethod(setterMethodName, newValue.getClass()).invoke(entity, newValue);
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Invalid field name or value.");
            }

            T updatedEntity = repository.save(entity);
            return ResponseEntity.ok(updatedEntity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
