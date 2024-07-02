package com.eurotech.tests;

import com.eurotech.pages.LoginPage;
import com.eurotech.utils.ConfigurationReader;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTests extends TestBase {

    @Test
    public void successLoginTest() {
        test.assignCategory("positive").info("Тест на авторизацию пользователя");
        context.driver.get(ConfigurationReader.get("base_url"));
        test.createNode("Шаг теста 1");
        test.createNode("Шаг теста 2");
        test.createNode("Шаг теста 3");
        test.warning("Это предупреждение!");
        assertTrue(new LoginPage(context)
                .loginAsStandardUser()
                .getFooterText()
                .contains("Sauce Labs"));
    }

    @Test
    public void emptyLoginTest() {
        test.assignCategory("negative");
        context.driver.get(ConfigurationReader.get("base_url"));

        assertEquals("Epic sadface: Username is required",
                new LoginPage(context).incorrectLoginAs("",""));
    }

    @Test
    public void blockedUserLoginTest() {
        test.assignCategory("negative");
        context.driver.get(ConfigurationReader.get("base_url"));
        assertEquals(
                "Epic sadface: Sorry, this user has been locked out.",
                new LoginPage(context).incorrectLoginAs("locked_out_user","secret_sauce")
        );
    }
}
