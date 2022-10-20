package com.novare.uploadfile.repositories;

import com.novare.uploadfile.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ContentRepository extends JpaRepository<Content, Long> {
}
