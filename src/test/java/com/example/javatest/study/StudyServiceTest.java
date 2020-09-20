package com.example.javatest.study;

import com.example.javatest.domain.Member;
import com.example.javatest.domain.Study;
import com.example.javatest.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {
    @InjectMocks
    StudyService studyService;

    @Mock
    MemberService memberService;

    @Mock
    StudyRepository studyRepository;

    @Test
    @DisplayName("Mock 객체 Return 되는 값 확인 메서드")
    void Mock_확인_테스트(){
        Optional<Member> member = memberService.findById(1l);
        assertEquals(Optional.empty(), member);
        memberService.validate(2L);

        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);
    }

    @Test
    @DisplayName("Stubbing 값 확인 예제")
    void stubbingStudyServiceTest(){
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("wonjin.jo@sk.com");

        when(memberService.findById(1L)).thenReturn(Optional.of(member));

//        when(memberService.findById(any())).thenReturn(Optional.of(member));          //any() 를 사용하면 어떤 매개변수건 같은 값을 return한다.
//        when(memberService.findById(1L)).thenThrow(new IllegalArgumentException());   //예외를 던지도록 설정 가능
//        when(memberService.findById(1L))
//                .thenReturn(Optional.of(member))
//                .thenThrow(new RuntimeException())
//                .thenReturn(Optional.empty());
        Optional<Member> byId = memberService.findById(1L);
        assertEquals("wonjin.jo@sk.com", byId.get().getEmail());

        doThrow(new IllegalArgumentException()).when(memberService).validate(1L);
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.validate(1L);
        });

        memberService.validate(2L);
//        Study study = new Study(10, "java");
//
//        studyService.createNewStudy(1L, study);
    }

    @Test
    @DisplayName("createStudy 메서드 테스트")
    void 스터디_만들기_테스트(){
        Member member = new Member();
        member.setId(1L);
        member.setEmail("wonjin.jo@sk.com");

        Study study = new Study(10, "spring study");

        when(memberService.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.save(study)).thenReturn(study);

        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Study newStudy = studyService.createNewStudy(1L, study);
        assertThat(newStudy.getName()).isEqualTo("spring study");
        assertThat(study.getOwnerId()).isEqualTo(member.getId());

        verify(memberService, times(1)).notify(study);
        verify(memberService, never()).validate(any());

        verifyNoMoreInteractions(memberService);

    }

    @Test
    @DisplayName("createStudy 메서드 테스트 BDD Style")
    void 스터디_만들기_테스트_BDD(){
        //given
//        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);

        Member member = new Member();
        member.setId(1L);
        member.setEmail("wonjin.jo@sk.com");

        Study study = new Study(10, "spring study");

        given(memberService.findById(1L)).willReturn(Optional.of(member));
        given(studyRepository.save(study)).willReturn(study);

        //when
        studyService.createNewStudy(1L, study);

        //then
        assertThat(study.getOwnerId()).isEqualTo(member.getId());
        then(memberService).should(times(1)).notify(study);
        then(memberService).should(times(1)).validate(1l);
        then(memberService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("Study Open 테스트")
    void openStudy(){
        //given
        assertNotNull(studyService);

        Study study = new Study(10, "spring study");
        assertNull(study.getOpenedDateTime());
        given(studyRepository.save(study)).willReturn(study);

        //when
        studyService.openStudy(study);

        //then
        assertThat(study.getStatus()).isEqualTo(StudyStatus.OPENED);
        assertThat(study.getOpenedDateTime()).isNotNull();
        then(memberService).should(times(1)).notify(study);
    }

    @Test
    void jpa_테스트(){
        Study study = new Study(10, "spring study");
        given(studyRepository.save(study)).willReturn(study);

        Study openStudy = studyService.openStudy(study);
        assertThat(study).isEqualTo(openStudy);
    }

}