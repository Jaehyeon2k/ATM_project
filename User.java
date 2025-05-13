public class User {
    private String id;
    private String password;
    private int balance;
    private int loginFailCount = 0;
    private boolean locked = false;

    public User(String id, String password) {
        this.id = id;
        this.password = password;
        this.balance = 0;
    }

    public String getId() {
        return id;
    }

    public boolean checkPassword(String pw) {
        return password.equals(pw);
    }

    public int getLoginFailCount() {
        return loginFailCount;
    }

    public boolean isLocked() {
        return locked;
    }

    public void unlock() {
        this.loginFailCount = 0;
        this.locked = false;
    }

    public boolean isAdmin() {
        return id.equals("admin");
    }

    public void increaseFailCount() {
        if (id.equals("admin")) {
            // 관리자 계정은 절대 잠기지 않게 처리
            System.out.println("관리자 계정은 잠기지 않습니다.");
            return;
        }

        loginFailCount++;
        if (loginFailCount >= 3) {
            locked = true;
        }
    }

    public void resetFailCount() {
        loginFailCount = 0;
    }

    public int getBalance() {
        return balance;
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public boolean withdraw(int amount) {
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }

    public void transfer(User receiver, int amount) {
        this.balance -= amount;
        receiver.deposit(amount);
    }
}
