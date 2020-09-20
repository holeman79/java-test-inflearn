package com.example.javatest;

import com.example.javatest.domain.Study;
import com.example.javatest.study.StudyStatus;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

class StudyTest {

    @Test
    @DisplayName("스터디 만들기 \uD83D\uDE31")
    void create_new_study() {
        assertThrows(IllegalArgumentException.class, () -> new Study(-10));

        Study study = new Study(10);
        assertNotNull(study);
        assertEquals(StudyStatus.DRAFT, study.getStatus(), "스터디를 처음 만들면 상태값이 Draft여야 한다.");
        assertTrue(study.getLimitCount() > 0, "스터디 최대 참석 가능 인원은 0보다 커야한다.");

//        assertAll(
//                () -> assertNotNull(study),
//                () -> assertEquals(StudyStatus.DRAFT, study.getStatus()),
//                () ->  assertTrue(study.getLimit() > 0, "스터디 최대 참석 가능 인원은 0보다 커야한다.")
//        );
    }

    @Test
    @DisplayName("Timeout 테스트")
    void create_new_study_timeout() {
        assertTimeout(Duration.ofMillis(100), () -> {
            new Study(10);
            Thread.sleep(300);
        });
    }

    @Test
    @DisplayName("Timeout 즉각적인 테스트")
    // 하지만 supply 부분을 별도의 thread로 실행하기 때문에 그냥 TImeout 테스트가 더 안전할 수 있다.
    void create_new_study_timeout_preemptively() {
        assertTimeoutPreemptively(Duration.ofMillis(100), () -> {
            new Study(10);
            Thread.sleep(300);
        });

    }

    @Test
    @DisplayName("조건에 따라 테스트 실행하기 assumeTrue")
    void test_method(){
        String test_env = System.getenv("TEST_ENV");
        System.out.println(test_env);
        assumeTrue("LOCAL".equalsIgnoreCase(test_env));

        Study study = new Study(10);
        assertThat(study.getLimitCount()).isGreaterThan(0);
    }

    @Test
    @DisplayName("조건에 따라 테스트 실행하기 assumingThat")
    void test_method2(){
        String test_env = System.getenv("TEST_ENV");
        System.out.println(test_env);

        assumingThat("LOCAL".equalsIgnoreCase(test_env), () -> {
            Study study = new Study(10);
            assertThat(study.getLimitCount()).isGreaterThan(0);
        });

    }

    @Test
    @DisplayName("조건에 따라 테스트 실행하기 @EnabledOnOs. @DisalbedOnOs")
    @EnabledOnOs({OS.MAC, OS.LINUX})
    //@DisabledOnOs(OS.MAC)
    void test_method3(){
        String test_env = System.getenv("TEST_ENV");
        System.out.println(test_env);
    }

    @Test
    @DisplayName("조건에 따라 테스트 실행하기 @EnabledOnJre")
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_11})
    void test_method4(){
        System.out.println("Enabled On Jre");
    }

    @Test
    @DisplayName("조건에 따라 테스트 실행하기 @EnabledIf 환경변수가 일치할 때 실행")
    @EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "LOCAL")
    void test_method5(){
        System.out.println("EnabledIfEnvironmentVariable");
    }

    @BeforeAll
    static void beforeAll(){
        System.out.println("before all");
    }

    @AfterAll
    static void afterAll(){
        System.out.println("after all");
    }

    @BeforeEach
    void beforeEach(){
        System.out.println("Before each");
    }

    @AfterEach
    void afterEach(){
        System.out.println("After each");
    }
}