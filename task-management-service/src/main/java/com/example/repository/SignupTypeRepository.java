package com.example.repository;

import com.example.model.SignupType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SignupTypeRepository extends JpaRepository<SignupType, Long> {

    @Query("select s from SignupType s where s.typeName = :typeName")
    Optional<SignupType> findSignupTypeByTypeName(@Param("typeName") String typeName);
}
