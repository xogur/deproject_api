package com.example.deproject_api.controller;

import com.example.deproject_api.domain.MusinsaAccount;
import com.example.deproject_api.domain.User;
import com.example.deproject_api.repository.AlertSettingRepository;
import com.example.deproject_api.repository.MusinsaAccountRepository;
import com.example.deproject_api.repository.UserRepository;
import com.example.deproject_api.util.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.deproject_api.domain.AlertSetting;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;





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

    private final MusinsaAccountRepository musinsaAccountRepository;

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

    @PostMapping("/musinsa")
    public ResponseEntity<String> registerMusinsa(@RequestBody Map<String, String> payload) {
        String userEmail = payload.get("email");
        String musinsaId = payload.get("musinsaId");
        String musinsaPassword = payload.get("musinsaPassword");

        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("사용자가 존재하지 않습니다.");
        }

        try {
            String encryptedPassword = AESUtil.encrypt(musinsaPassword);  // 🔐 암호화
            MusinsaAccount account = MusinsaAccount.builder()
                    .musinsaId(musinsaId)
                    .musinsaPassword(encryptedPassword)
                    .user(userOpt.get())
                    .build();

            musinsaAccountRepository.save(account);
            return ResponseEntity.ok("무신사 계정 등록 성공!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("암호화 중 오류 발생");
        }
    }

    @GetMapping("/musinsa")
    public ResponseEntity<?> getMusinsaAccount(@RequestParam String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 이메일의 사용자가 존재하지 않습니다.");
        }

        Optional<MusinsaAccount> accountOpt = musinsaAccountRepository.findByUser(userOpt.get());
        if (accountOpt.isPresent()) {
            MusinsaAccount account = accountOpt.get();

            // 비밀번호는 절대 반환하지 않고 ID만 반환
            Map<String, String> result = new HashMap<>();
            result.put("musinsaId", account.getMusinsaId());

            return ResponseEntity.ok(result);
        }

        return ResponseEntity.ok().build(); // 등록된 계정 없음
    }

    @PostMapping("/api/airflow/trigger")
    public ResponseEntity<String> triggerDag(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBasicAuth("airflow", "airflow"); // 인증 필요 시
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("conf", Map.of("user_email", email));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://localhost:8081/api/v1/dags/dags_sale_info/dagRuns",
                    request,
                    String.class
            );

            return ResponseEntity.status(response.getStatusCode()).body("DAG 실행 요청 완료!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Airflow 호출 실패: " + e.getMessage());
        }
    }

    private final AlertSettingRepository alertRepo;
    private final UserRepository userRepo;

    @PostMapping("/toggle")
    public ResponseEntity<String> toggleAlert(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        User user = userRepo.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("사용자 없음");
        }

        AlertSetting setting = alertRepo.findByUser(user).orElse(
                AlertSetting.builder().user(user).enabled(false).build()
        );

        setting.setEnabled(!setting.isEnabled());  // 상태 토글
        alertRepo.save(setting);

        return ResponseEntity.ok(setting.isEnabled() ? "✅ 알림 설정됨" : "🔕 알림 해제됨");
    }


}
