public class User {

    // 사용자 ID (계정 이름)
    private String id;

    // 사용자 비밀번호
    private String password;

    // 현재 계좌 잔액
    private int balance;

    // 로그인 실패 횟수 (3번 틀리면 잠금)
    private int loginFailCount = 0;

    // 계정이 잠겨있는지 여부
    private boolean locked = false;

    // 생성자: 새로운 사용자 객체를 만들 때 ID와 비밀번호를 지정
    public User(String id, String password) {
        this.id = id;
        this.password = password;
        this.balance = 0;  // 처음에는 잔액이 0원
    }

    // 사용자 ID를 반환하는 메서드
    public String getId() {
        return id;
    }

    // 비밀번호가 맞는지 확인하는 메서드
    public boolean checkPassword(String pw) {
        return password.equals(pw);
    }

    // 현재 로그인 실패 횟수를 반환
    public int getLoginFailCount() {
        return loginFailCount;
    }

    // 계정이 잠겼는지 확인
    public boolean isLocked() {
        return locked;
    }

    // 계정의 잠금을 해제 (로그인 실패 횟수도 초기화)
    public void unlock() {
        this.loginFailCount = 0;
        this.locked = false;
    }

    // 이 계정이 관리자(admin)인지 확인하는 메서드
    public boolean isAdmin() {
        return id.equals("admin");
    }

    // 로그인 실패 횟수를 증가시키는 메서드
    // 3회 이상 실패하면 계정을 잠금 상태로 변경
    public void increaseFailCount() {
        // 관리자 계정은 잠기지 않도록 예외 처리
        if (id.equals("admin")) {
            System.out.println("관리자 계정은 잠기지 않습니다.");
            return;
        }

        loginFailCount++;  // 실패 횟수 +1
        if (loginFailCount >= 3) {
            locked = true; // 3번 실패하면 계정 잠금
        }
    }

    // 로그인 성공 시 실패 횟수를 초기화
    public void resetFailCount() {
        loginFailCount = 0;
    }

    // 현재 잔액을 반환
    public int getBalance() {
        return balance;
    }

    // 입금: 전달받은 금액만큼 잔액 증가
    public void deposit(int amount) {
        balance += amount;
    }

    // 출금: 잔액이 충분하면 출금하고 true 반환, 부족하면 false 반환
    public boolean withdraw(int amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    // 송금: 현재 사용자에서 다른 사용자에게 금액을 보냄
    public void transfer(User receiver, int amount) {
        this.balance -= amount;       // 내 잔액 차감
        receiver.deposit(amount);     // 상대방 잔액 증가
    }
}
