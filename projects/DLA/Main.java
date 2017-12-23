import java.util.Random;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Dimension;
public class Main {

    static final int width = 600;
    static final int height = width;
    static byte[] buffer = new byte[width*height];
    static BufferedImage img;
    static WritableRaster raster;

    public static void main(String[] args) {
        
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        raster = img.getRaster();
        
        JFrame frame = new JFrame("Diffusion Limited Aggregation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, null);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));
        frame.add(panel);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);

        Random rand = new Random();
        for (int i = 0; i < 100000; i++) {
            int x = width/2;
            int y = height/2;
            for (int j = 0; j < 2000; j++) {
                int r = rand.nextInt(8);
                if(r%2 == 0) {
                    r/=2;
                    if(r%2 == 0) {
                        r/=2;
                        x += r%2 == 0 ? 1:-1;
                    }
                    else {
                        r/=2;
                        y += r%2 == 0 ? 1:-1;
                    }
                }
                else {
                    r/=2;
                    x += r%2 == 0 ? 1:-1;
                    r/=2;
                    y += r%2 == 0 ? 1:-1;
                }
            }
        if (x>=0 && y>=0 && x<width && y<height)
            buffer[y*width + x] = 1;
        }
        
        int[] pixels = new int[width*height];
        for (int i = 0; i < width*height; i++) {
            if (buffer[i] == 1) {
                pixels[i] = 0xFFFFFF;
            }
            else {
                pixels[i] = 0x000000;
            }
        }
        raster.setDataElements(0, 0, width, height, pixels);
        
        panel.repaint();

    }
}
