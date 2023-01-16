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
        Assertions.assertTrue(mojangService.isExistingMinecraftUser("Synthesyzer"));
        Assertions.assertTrue(mojangService.isExistingMinecraftUser("_Tony_"));
        Assertions.assertTrue(mojangService.isExistingMinecraftUser("Riboto"));
        Assertions.assertFalse(mojangService.isExistingMinecraftUser("NonExistingUserThisNameShouldDefinitelyNotExist"));
    }

}
