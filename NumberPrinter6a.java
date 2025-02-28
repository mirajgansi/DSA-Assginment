import java.util.concurrent.Semaphore;

class NumberPrinter6a {
    public void printZero() {
        System.out.print("0");
    }

    public void printEven(int n) {
        System.out.print(n);
    }

    public void printOdd(int n) {
        System.out.print(n);
    }
}

class ThreadController {
    private int n;
    private NumberPrinter6a printer;
    private Semaphore zeroSemaphore = new Semaphore(1);
    private Semaphore evenSemaphore = new Semaphore(0);
    private Semaphore oddSemaphore = new Semaphore(0);

    public ThreadController(int n, NumberPrinter6a printer) {
        this.n = n;
        this.printer = printer;
    }

    public void zero() {
        try {
            for (int i = 1; i <= n; i++) {
                zeroSemaphore.acquire();
                printer.printZero();
                if (i % 2 == 0) {
                    evenSemaphore.release();
                } else {
                    oddSemaphore.release();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void even() {
        try {
            for (int i = 2; i <= n; i += 2) {
                evenSemaphore.acquire();
                printer.printEven(i);
                zeroSemaphore.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void odd() {
        try {
            for (int i = 1; i <= n; i += 2) {
                oddSemaphore.acquire();
                printer.printOdd(i);
                zeroSemaphore.release();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Main {
    public static void main(String[] args) {
        int n = 5;
        NumberPrinter6a printer = new NumberPrinter6a();
        ThreadController controller = new ThreadController(n, printer);
        Thread zeroThread = new Thread(controller::zero);
        Thread evenThread = new Thread(controller::even);
        Thread oddThread = new Thread(controller::odd);
        zeroThread.start();
        evenThread.start();
        oddThread.start();
    }
}