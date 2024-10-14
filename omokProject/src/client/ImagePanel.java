package client;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;


class ImagePanel extends JPanel {
    private Image image;

    public void setImage(Image image) {
        this.image = image;
        repaint(); // 패널을 다시 그리기
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image != null) {
            // 패널 크기
            int panelWidth = this.getWidth();
            int panelHeight = this.getHeight();

            // 이미지 크기
            int imageWidth = image.getWidth(this);
            int imageHeight = image.getHeight(this);

            // 이미지가 중앙에 오도록 위치 계산
            int x = (panelWidth - imageWidth) / 2;
            int y = (panelHeight - imageHeight) / 2;

            // 이미지 그리기
            g.drawImage(image, x, y, this);
        }
    }
}

