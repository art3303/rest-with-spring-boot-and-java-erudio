package br.com.erudio.restwithspringbootandjavaerudio.unittests.mockito.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import br.com.erudio.restwithspringbootandjavaerudio.data.vo.v1.PersonVO;
import br.com.erudio.restwithspringbootandjavaerudio.exceptions.RequiredObjectIsNullException;
import br.com.erudio.restwithspringbootandjavaerudio.model.Person;
import br.com.erudio.restwithspringbootandjavaerudio.repositories.PersonRepository;
import br.com.erudio.restwithspringbootandjavaerudio.services.PersonServices;
import br.com.erudio.restwithspringbootandjavaerudio.unittests.mapper.mocks.MockPerson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)

 class PersonServicesTest {

  MockPerson input;

  @InjectMocks
  private PersonServices service;

  @Mock
  PersonRepository repository;

  @BeforeEach
  void setUpMocks() throws Exception {
   input = new MockPerson();
   MockitoAnnotations.openMocks(this);
  }

  @Test
  void testFindById() {
   Person entity = input.mockEntity(1);
   entity.setId(1L);

   when(repository.findById(1L)).thenReturn(Optional.of(entity));

   var result = service.findById(1L);

   assertNotNull(result);
   assertNotNull(result.getKey());
   assertNotNull(result.getLinks());
   assertTrue(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
   assertEquals("Addres Test1", result.getAddress());
   assertEquals("First Name Test1", result.getFirstName());
   assertEquals("Last Name Test1", result.getLastName());
   assertEquals("Female", result.getGender());
  }

  @Test
  void testCreate() {
   Person entity = input.mockEntity(1);

   Person persisted = entity;
   persisted.setId(1L);

   PersonVO vo = input.mockVO(1);
   vo.setKey(1L);

   when(repository.save(entity)).thenReturn(persisted);

   var result = service.create(vo);
   assertNotNull(result);
   assertNotNull(result.getKey());
   assertNotNull(result.getLinks());
   assertTrue(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
   assertEquals("Addres Test1", result.getAddress());
   assertEquals("First Name Test1", result.getFirstName());
   assertEquals("Last Name Test1", result.getLastName());
   assertEquals("Female", result.getGender());
  }

  @Test
  void testCreateWithNullPerson() {
   Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
    service.create(null);
   });

   String expectedMessage = "It is not allowed to persist a null object!";
   String actualMessage =  exception.getMessage();

  assertTrue(actualMessage.contains(expectedMessage));
 }

 @Test
 void testUpdateWithNullPerson() {
  Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
   service.update(null);
  });

  String expectedMessage = "It is not allowed to persist a null object!";
  String actualMessage =  exception.getMessage();

  assertTrue(actualMessage.contains(expectedMessage));
 }

  @Test
  void testUpdate() {
  Person entity = input.mockEntity(1);
  entity.setId(1L);

  Person persisted = entity;
  persisted.setId(1L);

  PersonVO vo = input.mockVO(1);
  vo.setKey(1L);

  when(repository.findById(1L)).thenReturn(Optional.of(entity));
  when(repository.save(entity)).thenReturn(persisted);

  var result = service.update(vo);

  assertNotNull(result);
  assertNotNull(result.getKey());
  assertNotNull(result.getLinks());
  assertTrue(result.toString().contains("links: [</api/person/v1/1>;rel=\"self\"]"));
  assertEquals("Addres Test1", result.getAddress());
  assertEquals("First Name Test1", result.getFirstName());
  assertEquals("Last Name Test1", result.getLastName());
  assertEquals("Female", result.getGender());
 }

 @Test
 void testDelete() {
  Person entity = input.mockEntity(1);
  entity.setId(1L);

  when(repository.findById(1L)).thenReturn(Optional.of(entity));

  service.delete(1L);
 }
}
