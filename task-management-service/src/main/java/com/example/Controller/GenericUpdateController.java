package com.example.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;
import java.util.Set;
public abstract class GenericUpdateController<T, ID> {

    @Autowired
    private JpaRepository<T, ID> repository;

    private static final Set<String> IMMUTABLE_FIELDS = Set.of("id", "createdDate"); // Add other immutable fields here

    @PatchMapping("/{id}/{fieldName}")
    public ResponseEntity<?> updateField(@PathVariable ID id, @PathVariable String fieldName, @RequestBody Object newValue) {
        if (IMMUTABLE_FIELDS.contains(fieldName.toLowerCase())) {
            return ResponseEntity.badRequest().body(fieldName + " cannot be modified.");
        }
        Optional<T> optionalEntity = repository.findById(id);

        if (optionalEntity.isPresent() ) {
            T entity = optionalEntity.get();

            try {
                String setterMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                entity.getClass().getMethod(setterMethodName, newValue.getClass()).invoke(entity, newValue);
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
