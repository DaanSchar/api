package com.voidhub.api.mojang;

import com.voidhub.api.BaseTest;
import com.voidhub.api.service.MojangService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestMojangService extends BaseTest {

    private @Autowired MojangService mojangService;

    @Test
    public void testMojangServiceForExistingUsers() {
        Assertions.assertFalse(mojangService.getMinecraftUserInfo("Synthesyzer").isEmpty());
        Assertions.assertFalse(mojangService.getMinecraftUserInfo("_Tony_").isEmpty());
        Assertions.assertFalse(mojangService.getMinecraftUserInfo("Riboto").isEmpty());
        Assertions.assertTrue(mojangService.getMinecraftUserInfo("NonExistingUserThisNameShouldDefinitelyNotExist").isEmpty());
    }

}
