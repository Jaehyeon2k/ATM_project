import java.util.*;

public class BankSystem {
    private List<User> users = new ArrayList<>();
    private User currentUser = null;
    private Scanner scanner = new Scanner(System.in);

    public void run() {
        while (true) {
            if (currentUser == null) {
                System.out.println("\n=== ATM 시스템 ===");
                System.out.println("1. 회원가입");
                System.out.println("2. 로그인");
                System.out.println("3. 종료");
                System.out.print(">> ");

                int choice;
                try {
                    choice = Integer.parseInt(scanner.nextLine());  // 안전하게 문자열로 받고 변환
                } catch (NumberFormatException e) {
                    System.out.println("숫자만 입력하세요!");
                    continue;  // 메뉴 다시 출력
                }

                switch (choice) {
                    case 1 -> signUp();
                    case 2 -> login();
                    case 3 -> {
                        System.out.println("종료합니다.");
                        return;
                    }
                    default -> System.out.println("잘못된 선택입니다.");
                }
            } else {
                showMenu();
            }
        }
    }

    private void signUp() {
        System.out.print("ID 입력: ");
        String id = scanner.nextLine();

        if (id.equals("admin")) {
            System.out.println("이 ID는 사용할 수 없습니다.");
            return;
        }

        if (getUserById(id) != null) {
            System.out.println("이미 존재하는 ID입니다.");
            return;
        }

        System.out.print("비밀번호 입력: ");
        String pw = scanner.nextLine();
        users.add(new User(id, pw));
        System.out.println("회원가입 성공!");
    }


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
            user.resetFailCount();
            System.out.println("로그인 성공!");
        } else {
            user.increaseFailCount();
            int remain;

            if (user.isLocked()) {
                remain = 0;
            } else {
                remain = 3 - user.getLoginFailCount();
            }

            System.out.println("비밀번호가 틀렸습니다. 남은 시도 횟수: " + remain);
            if (user.isLocked()) {
                System.out.println("계정이 잠겼습니다. 관리자에게 문의하세요.");
            }
        }
    }


    private void logout() {
        System.out.println("로그아웃되었습니다.");
        currentUser = null;
    }

    private void showMenu() {
        while (currentUser != null) {
            System.out.println("\n--- ATM 메뉴 ---");
            System.out.println("1. 입금");
            System.out.println("2. 출금");
            System.out.println("3. 송금");
            System.out.println("4. 잔액 조회");
            System.out.println("5. 로그아웃");

            if (currentUser.isAdmin()) {
                System.out.println("6. 사용자 잠금 해제");  // 관리자 전용
            }

            System.out.print(">> ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());  // 안전하게 문자열로 받고 변환
            } catch (NumberFormatException e) {
                System.out.println("숫자만 입력하세요!");
                continue;  // 메뉴 다시 출력
            }

            switch (choice) {
                case 1 -> deposit();
                case 2 -> withdraw();
                case 3 -> transfer();
                case 4 -> checkBalance();
                case 5 -> logout();
                case 6 -> {
                    if (currentUser.isAdmin()) {
                        unlockUser();
                    } else {
                        System.out.println("잘못된 선택입니다.");
                    }
                }
                default -> System.out.println("잘못된 선택입니다.");
            }
        }
    }

    public BankSystem() {
        // admin 계정 미리 등록
        users.add(new User("admin", "admin123"));
    }

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
        scanner.nextLine();

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

    private void checkBalance() {
        System.out.println("현재 잔액: " + currentUser.getBalance() + "원");
    }

    private User getUserById(String id) {
        for (User user : users) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
    }
}
