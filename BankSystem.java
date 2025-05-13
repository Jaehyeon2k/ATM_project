import java.util.*;

public class BankSystem {

    // 전체 사용자 정보를 담는 리스트
    private List<User> users = new ArrayList<>();

    // 현재 로그인된 사용자
    private User currentUser = null;

    // 사용자 입력을 받기 위한 스캐너
    private Scanner scanner = new Scanner(System.in);

    // 프로그램의 메인 루프
    public void run() {
        while (true) {
            if (currentUser == null) {
                // 로그인 전 화면
                System.out.println("\n=== ATM 시스템 ===");
                System.out.println("1. 회원가입");
                System.out.println("2. 로그인");
                System.out.println("3. 종료");
                System.out.print(">> ");

                int choice;
                try {
                    // 문자열로 받고 정수로 변환하여 예외 방지
                    choice = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("숫자만 입력하세요!");
                    continue;
                }

                switch (choice) {
                    case 1 -> signUp();    // 회원가입
                    case 2 -> login();     // 로그인
                    case 3 -> {
                        System.out.println("종료합니다.");
                        return;            // 프로그램 종료
                    }
                    default -> System.out.println("잘못된 선택입니다.");
                }
            } else {
                // 로그인 후 메뉴
                showMenu();
            }
        }
    }

    // 사용자 회원가입 처리
    private void signUp() {
        System.out.print("ID 입력: ");
        String id = scanner.nextLine();

        if (id.equals("admin")) {
            System.out.println("이 ID는 사용할 수 없습니다.");  // 관리자 ID 사용 금지
            return;
        }

        if (getUserById(id) != null) {
            System.out.println("이미 존재하는 ID입니다.");  // 중복 ID 방지
            return;
        }

        System.out.print("비밀번호 입력: ");
        String pw = scanner.nextLine();
        users.add(new User(id, pw));
        System.out.println("회원가입 성공!");
    }

    // 사용자 로그인 처리
    private void login() {
        System.out.print("ID 입력: ");
        String id = scanner.nextLine();
        System.out.print("비밀번호 입력: ");
        String pw = scanner.nextLine();

        User user = getUserById(id);
        if (user == null) {
            System.out.println("존재하지 않는 ID입니다.");
            return;
        }

        if (user.isLocked()) {
            System.out.println("이 계정은 잠겨 있습니다. 관리자에게 문의하세요.");
            return;
        }

        if (user.checkPassword(pw)) {
            currentUser = user;
            user.resetFailCount();  // 로그인 성공 시 실패횟수 초기화
            System.out.println("로그인 성공!");
        } else {
            user.increaseFailCount();  // 실패횟수 증가

            int remain;
            if (user.isLocked()) {
                remain = 0;
            } else {
                remain = 3 - user.getLoginFailCount();  // 남은 시도 횟수 계산
            }

            System.out.println("비밀번호가 틀렸습니다. 남은 시도 횟수: " + remain);
            if (user.isLocked()) {
                System.out.println("계정이 잠겼습니다. 관리자에게 문의하세요.");
            }
        }
    }

    // 로그아웃 처리
    private void logout() {
        System.out.println("로그아웃되었습니다.");
        currentUser = null;
    }

    // 로그인된 사용자에게 보이는 메뉴
    private void showMenu() {
        while (currentUser != null) {
            System.out.println("\n--- ATM 메뉴 ---");
            System.out.println("1. 입금");
            System.out.println("2. 출금");
            System.out.println("3. 송금");
            System.out.println("4. 잔액 조회");
            System.out.println("5. 로그아웃");

            if (currentUser.isAdmin()) {
                System.out.println("6. 사용자 잠금 해제");  // 관리자 메뉴
            }

            System.out.print(">> ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("숫자만 입력하세요!");
                continue;
            }

            switch (choice) {
                case 1 -> deposit();      // 입금
                case 2 -> withdraw();     // 출금
                case 3 -> transfer();     // 송금
                case 4 -> checkBalance(); // 잔액 확인
                case 5 -> logout();       // 로그아웃
                case 6 -> {
                    if (currentUser.isAdmin()) {
                        unlockUser();    // 관리자일 때만 잠금 해제
                    } else {
                        System.out.println("잘못된 선택입니다.");
                    }
                }
                default -> System.out.println("잘못된 선택입니다.");
            }
        }
    }

    // BankSystem 생성자 - 실행 시 관리자 계정 자동 추가
    public BankSystem() {
        users.add(new User("admin", "admin123"));  // 관리자 계정 하드코딩 등록
    }

    // 관리자 전용: 사용자 잠금 해제 기능
    private void unlockUser() {
        System.out.print("잠금 해제할 사용자 ID 입력: ");
        String targetId = scanner.nextLine();
        User targetUser = getUserById(targetId);

        if (targetUser == null) {
            System.out.println("사용자를 찾을 수 없습니다.");
            return;
        }

        if (!targetUser.isLocked()) {
            System.out.println("이 사용자는 잠겨 있지 않습니다.");
            return;
        }

        targetUser.unlock();
        System.out.println("[" + targetId + "] 잠금 해제 완료!");
    }

    // 입금 기능
    private void deposit() {
        System.out.print("입금할 금액: ");
        int amount = scanner.nextInt();
        if (amount <= 0) {
            System.out.println("금액은 0보다 커야 합니다.");
            return;
        }
        currentUser.deposit(amount);
        System.out.println("입금 완료. 현재 잔액: " + currentUser.getBalance() + "원");
    }

    // 출금 기능
    private void withdraw() {
        System.out.print("출금할 금액: ");
        int amount = scanner.nextInt();
        if (amount <= 0) {
            System.out.println("금액은 0보다 커야 합니다.");
            return;
        }
        if (currentUser.withdraw(amount)) {
            System.out.println("출금 완료. 현재 잔액: " + currentUser.getBalance() + "원");
        } else {
            System.out.println("잔액이 부족합니다.");
        }
    }

    // 송금 기능
    private void transfer() {
        System.out.print("송금할 사용자 ID: ");
        String targetId = scanner.nextLine();
        User receiver = getUserById(targetId);

        if (receiver == null) {
            System.out.println("존재하지 않는 사용자입니다.");
            return;
        }

        if (receiver == currentUser) {
            System.out.println("자기 자신에게 송금할 수 없습니다.");
            return;
        }

        System.out.print("송금할 금액: ");
        int amount = scanner.nextInt();
        scanner.nextLine();  // 버퍼 비우기

        if (amount <= 0) {
            System.out.println("금액은 0보다 커야 합니다.");
            return;
        }

        if (currentUser.getBalance() < amount) {
            System.out.println("잔액이 부족합니다.");
            return;
        }

        currentUser.transfer(receiver, amount);
        System.out.println("송금 완료. 현재 잔액: " + currentUser.getBalance() + "원");
    }

    // 잔액 조회 기능
    private void checkBalance() {
        System.out.println("현재 잔액: " + currentUser.getBalance() + "원");
    }

    // 사용자 ID로 해당 User 객체 찾기
    private User getUserById(String id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;  // 못 찾으면 null 반환
    }
}
