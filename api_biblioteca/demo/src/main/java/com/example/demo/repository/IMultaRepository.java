package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entities.Multa;

@Repository
public interface IMultaRepository extends JpaRepository<Multa, Long>{
}


