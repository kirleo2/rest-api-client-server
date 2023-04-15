package cz.fit.cvut.tjv.game_store.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.fit.cvut.tjv.game_store.api.model.UserDto;
import cz.fit.cvut.tjv.game_store.api.model.converter.UserToDtoConverter;
import cz.fit.cvut.tjv.game_store.api.model.converter.UserToEntityConverter;
import cz.fit.cvut.tjv.game_store.buisness.UserService;
import cz.fit.cvut.tjv.game_store.buisness.exceptions.EntityAlreadyExistsException;
import cz.fit.cvut.tjv.game_store.buisness.exceptions.EntityDoesNotExistsException;
import cz.fit.cvut.tjv.game_store.domain.Game;
import cz.fit.cvut.tjv.game_store.domain.Order;
import cz.fit.cvut.tjv.game_store.domain.User;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService service;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    private UserToDtoConverter toDtoConverter;
    @MockBean
    private UserToEntityConverter toEntityConverter;

    private UserDto testDto;
    private User testUser;


    @BeforeEach
    void setUp(){
        testDto = new UserDto();
        testUser = new User("test", "Test", "Test", "test@gmail.com", "CZ", 100f);

        testDto.setUsername("test");
        testDto.setEmail("test@gmail.com");
        testDto.setFirstName("Test");
        testDto.setLastName("Test");
        testDto.setCountry("CZ");
        testDto.setBalance(100f);

        var game1 = new Game(1L, "Test Game 1", 10f, "");
        var game2 = new Game(2L, "Test Game 2", 20f, "");
        var game3 = new Game(3L, "Test Game 3", 30f, "");

        var order = new Order(1L, testUser, LocalDate.now(), 10f, new HashSet<>(Arrays.asList(game1, game3)), false);
        var order2 = new Order(2L, testUser, LocalDate.now(), 10f, new HashSet<>(List.of(game2)), false);

        testUser.addOrder(order);
        testUser.addOrder(order2);

    }

    @Test
    void testCreateUserTest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateUserExistingUser() throws Exception {
        Mockito.when(toEntityConverter.apply(Mockito.any(UserDto.class))).thenReturn(testUser);
        Mockito.doThrow(new EntityAlreadyExistsException()).when(service).create(testUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testDto)))
                .andExpect(status().isConflict());
    }
    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/test").accept("application/json"))
                .andExpect(status().isOk());
        Mockito.verify(service, Mockito.times(1)).deleteByID(testUser.getID());

        Mockito.doThrow(new EntityDoesNotExistsException()).when(service).deleteByID("test2");
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/test2").accept("application/json"))
                .andExpect(status().isNotFound());
    }
    @Test
    void testReadAllUserOrders() throws Exception{
        Mockito.when(service.readByID("test")).thenReturn(Optional.of(testUser));
        mockMvc.perform(get("/users/test/orders").accept("application/json")).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].username", Matchers.is("test")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].username", Matchers.is("test")));

    }



}
