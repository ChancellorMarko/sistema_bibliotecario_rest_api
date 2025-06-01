package com.example.demo.Entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "livros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Livro {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


@Column(nullable = false)
private String titulo;


@Column(nullable = false)
private String autor;


@Column(nullable = false)
private String editora; // NOVO CAMPO


@Column(nullable = false)
private Integer anoPublicacao; // NOVO CAMPO


@Column(nullable = false, unique = true)
private String isbn;


@Column(nullable = false)
private Integer quantidade;


@Column(nullable = false)
private String categoria;
}
