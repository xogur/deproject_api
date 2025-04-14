package com.example.deproject_api.controller;

import com.example.deproject_api.domain.User;
import com.example.deproject_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// REST API 컨트롤러 역할
@RestController
// final로 선언된 필드를 자동으로 생성자 주입해줘 (→ 여기선 userRepository).
@RequiredArgsConstructor
//이 컨트롤러의 모든 URL 앞에 /api/users가 붙어.
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")  // CORS 허용 (개발 시에는 *로, 배포 시에는 도메인 지정)
public class UserController {

    // UserRepository는 DB에 접근하는 DAO(데이터 접근 객체).
    private final UserRepository userRepository;

    // ✅ 회원가입 /signup으로 post요청이 발생하면 실행
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) { //User객체는 내가 domain에서 정의한 User, 프론트가 JSON데이터를 보내면 그 요청을 User에 담음
        // 만약 요청받은 이메일이 DB에 존재한다면
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("이미 사용 중인 이메일입니다.");
        }

        // 존재하지 않는다면 DB에 저장
        userRepository.save(user);
        return ResponseEntity.ok("회원가입 성공!");
    }

    // ✅ 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginUser) { // 프론트에서 넘어온 데이터를 User객체에  loginUser란 이름으로 저장
        // Optional<User> 는 User형태로 저장하는데 optionalUser에는 값이 있을수도 있고 null일수도 있음
        Optional<User> optionalUser = userRepository.findByEmail(loginUser.getEmail());

        // 만약 null이라면
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("이메일이 존재하지 않습니다.");
        }

        // user에는 찾아진 이메일과 일치하는 name과 pw데이터 들을 가져옴
        User user = optionalUser.get();
        if (!user.getPassword().equals(loginUser.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀렸습니다.");
        }

        return ResponseEntity.ok("로그인 성공!");
    }
}
