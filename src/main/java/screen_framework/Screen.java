package screen_framework;

public interface Screen {
    void update();
    void draw();
    void mousePressed(int x, int y);
    void keyPressed(char key);
}
