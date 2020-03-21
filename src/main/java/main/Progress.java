package main;


import com.google.common.util.concurrent.AtomicDouble;
import javafx.scene.control.ProgressBar;

public class Progress implements Runnable {

    ProgressBar progressBar;
    AtomicDouble progress;

    Progress(AtomicDouble progress, ProgressBar progressBar) {
        this.progress = progress;
        this.progressBar = progressBar;
    }

    @Override
    public void run() {
        progressBar.setProgress(0);
        while(progress.get() < .82){
            progressBar.setProgress(progress.get());
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}