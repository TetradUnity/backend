package com.tetradunity.server.services;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CheckValidServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CheckValidService checkValidService;

    @Test
    public void checkUserTest(){
        Mockito.when(userRepository.findByEmail("maksimmankivskiy03@gmail.com")).thenReturn(user2);
        Mockito.when(userRepository.findByEmail("maksimmankivskiy04@gmail.com")).thenReturn(user1);

        String result = checkValidService.checkUser("maksimmankivskiy04@gmail.com", "q21we43rt65y", "Максим", "Маньківський", true);
        Assertions.assertEquals("user_already_exist", result);

        result = checkValidService.checkUser("maksimmankivskiy03@gmail.com", "q21we43rt65y", "Максим", "Маньківський", true);
        Assertions.assertEquals("ok", result);

        result = checkValidService.checkUser("maksimmankivskiy03@gmail.com", "q21we43rt65y", "Maksim", "Маньківський", true);
        Assertions.assertEquals("incorrect_name", result);
    }

    private Optional<UserEntity> user1 = Optional.of(new UserEntity());
    private Optional<UserEntity> user2 = Optional.empty();
}
