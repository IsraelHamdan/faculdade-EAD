package com.jdev.student.repository;

import com.jdev.student.model.externalClasses.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TeacherRepository  extends JpaRepository<Teacher, UUID> {



}
