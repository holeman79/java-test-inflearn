package com.example.javatest;

import com.example.javatest.domain.Study;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * 각각의 메소드는 새로운 인스턴스를 생성한다.
 * Lifecycle.PER_CLASS를 사용하면 한 클래스 내의 테스트 메소드가 인스턴스를 공유한다
 * @TestMethodOrder 어노테이션을 통해 메소드 실행순서를 정할 수 있다.
 */

//@ExtendWith(FindSlowTestExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudyTest_step3 {

    @RegisterExtension
    static FindSlowTestExtension findSlowTestExtension = new FindSlowTestExtension(1000L);

    @Order(2)
    @FastTest
    @DisplayName("스터디 만들기 fast")
    void create_new_study() {
        System.out.println(this);
        Study study = new Study(100);
        assertThat(study.getLimitCount()).isGreaterThan(0);
    }

    @Order(1)
    @Test
    @DisplayName("스터디 만들기 slow")
    void create_new_study_agaRin() throws InterruptedException {
        Thread.sleep(1005L);
        System.out.println(this);
        Study study = new Study(100);
        assertThat(study.getLimitCount()).isGreaterThan(0);
    }



    //Lifecycle이 per class면 beforeAll, afterAll을 static으로 선언할 필요가 없음.
    @BeforeAll
    void beforeAll(){
        System.out.println("before all");
    }

    @AfterAll
    void afterAll(){
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