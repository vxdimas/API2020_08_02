import com.google.common.collect.Ordering;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;


public class ApiTest {

    @Test
    /*
    Тест - кейс 1
    1.	Используя сервис https://reqres.in/ получить список пользователей со второй страницы.
    2.	Убедится что аватары пользователей совпадают
    Автотесты необходимо написать, используя данный стек:
    API
     */
    public void testCaseUserAvatars() {
        String avatar = "128.jpg";  // Аватар, используемый в системе по умолчанию, с которым необходимо провести совпадение
        Response testing = given()
                .contentType("application/json")
                .when()
                .get("https://reqres.in/api/users?page=2")
                .then()
                .extract()
                .response();
        List<String> listOfUserAvatars = testing.jsonPath().get("data.avatar");
        Assert.assertTrue(
                listOfUserAvatars.stream().allMatch(x->x.toString().contains(avatar))
                , "Аватары пользователей не совпадают"
        );
    }

    @Test
    /*
    Тест - кейс 2
    1.	Используя сервис https://reqres.in/ протестировать регистрацию пользователя в системе.
    2.	Необходимо создание двух тестов на успешный логин и логин с ошибкой из-за не введённого пароля
    Автотест необходимо написать, используя данный стек:
    API
     */
    public void testCaseRegistrationAndLogin() {
        Map<String,String> data = new HashMap<>();
        data.put("email","eve.holt@reqres.in");
        data.put("password","pistol");
        String tokenSuccess = "QpwL5tke4Pnpja7X4";  // токен для успешной регистрации и логина в системе
        //--------------------------Проверка регистрации
        Response responceRegistration = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post("https://reqres.in/api/register")
                .then()
                .log().all()
                .extract()
                .response();
        JsonPath jsonResponceRegistration = responceRegistration.jsonPath();
        Assert.assertEquals(jsonResponceRegistration.get("token").toString(),tokenSuccess, "Failed registration"); // проверяем регистрацию в системе

        // --------------------------Проверка логина с поролем
        Response responceLogin = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post("https://reqres.in/api/login")
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();
        JsonPath jsonResponceLogin = responceLogin.jsonPath();
        Assert.assertEquals(jsonResponceLogin.get("token").toString(),tokenSuccess, "Failed login"); // проверяем успешный вход c поролем

        // --------------------------Проверка логина без пороля
        data.remove("password");
        Response responceLoginFail = given()
                .contentType("application/json")
                .body(data)
                .when()
                .post("https://reqres.in/api/login")
                .then()
                .log().all()
                .statusCode(400)
                .extract()
                .response();
        JsonPath jsonResponceLoginFail = responceLoginFail.jsonPath();
        Assert.assertEquals(jsonResponceLoginFail.get("error").toString(),"Missing password", "Failed script"); // проверяем наличие ошибки при попытке входе в систему без пороля
    }

    @Test
    /*
    Тест - кейс 3
    Используя сервис https://reqres.in/ убедится что операция LIST <RESOURCE> возвращает данные отсортированные по годам
     */
    public void testCaseSortDataYear() {
        Response testing = given()
                .contentType("application/json")
                .when()
                .get("https://reqres.in/api/unknown")
                .then()
                .extract()
                .response();
        List<Integer> listOfYeats = testing.jsonPath().get("data.year");
        //System.out.println(listOfYeats);
        Assert.assertTrue(Ordering.natural().isOrdered(listOfYeats),"Data is not sorted by year");
    }
}
