package com.example.javatest.study;

import com.example.javatest.domain.Member;
import com.example.javatest.domain.Study;
import com.example.javatest.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Testcontainers
@Slf4j
@ContextConfiguration(initializers = StudyServiceDockerTest.ContainerPropertyInitializer.class)
class StudyServiceDockerTest {

    @Mock
    MemberService memberService;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    Environment environment;

    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer()
            .withDatabaseName("studytest");

//    @Container
//    static GenericContainer postgreSQLContainer = new GenericContainer("postgres")
//            .withExposedPorts(5432)
//            .withEnv("POSTGRES_DB", "studytest");

    @BeforeAll
    static void beforeAll(){
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(log);
        postgreSQLContainer.followOutput(logConsumer);
    }

    @BeforeEach
    void beforeEach(){
        System.out.println("==============");
        System.out.println(environment.getProperty("container.port"));
        studyRepository.deleteAll();
    }

    @Test
    @DisplayName("createStudy 메서드 테스트")
    void 스터디_만들기_테스트(){
        Member member = new Member();
        member.setId(1L);
        member.setEmail("wonjin.jo@sk.com");

        Study study = new Study(10, "spring study");

        when(memberService.findById(1L)).thenReturn(Optional.of(member));

        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Study newStudy = studyService.createNewStudy(1L, study);
        assertThat(newStudy.getName()).isEqualTo("spring study");
        assertThat(study.getOwnerId()).isEqualTo(member.getId());

        verify(memberService, times(1)).notify(study);
        verify(memberService, never()).validate(any());

        verifyNoMoreInteractions(memberService);

    }

    static class ContainerPropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>{
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of("container.port=" + postgreSQLContainer.getMappedPort(5432))
                    .applyTo(context.getEnvironment());
        }
    }

}