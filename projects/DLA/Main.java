import java.util.Random;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferInt;
import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Color;
import java.io.IOException;
import java.io.File;
public class Main {

    static final int width = 1600;
    static final int height = width;
    static int[] buffer = new int[width*height];
    static BufferedImage img;
    static WritableRaster raster;

    public static void setupBuffer() {
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //Graphics g = img.getGraphics();
        //g.setColor(new Color(0,0,1));
        //g.fillOval(width/2-50,height/2-50,100,100);
        //g.drawOval(150, 130, 300, 300);
        //g.fillOval(width/2,height/2,2,2);
        //g.drawLine(0,0, width,height);
        //g.drawLine(0,height, width,0);
        /*int tx = (int)(200.0*Math.cos(Math.PI/3.0));
        int ty = (int)(200.0*Math.sin(Math.PI/3.0));
        g.drawLine(100,height/2, 500,height/2);
        g.drawLine(width/2-tx,height/2-ty, width/2+tx,height/2+ty);
        g.drawLine(width/2-tx,height/2+ty, width/2+tx,height/2-ty);*/
        buffer = ((DataBufferInt)img.getRaster().getDataBuffer()).getData();
        int tr = 100;
        for (int y = height/2 - tr; y < height/2 + tr; y++) {
            for (int x = width/2 - tr; x < width/2 + tr; x++) {
                int tx = x - width/2;
                int ty = y - height/2;
                double l = Math.sqrt((double)tx*tx+(double)ty*ty)/tr;
                l = Math.sqrt(1.0-l*l);
                if (l>1.0) continue;
                int b = (int) (255.0*Math.pow(l*l*l*l*l*l*l*0.7 + l/3.0 + 0.05, 0.45));
                if (b>255) b=255;
                buffer[y*width + x] = ((int)b & 0xFF) << 16 | ((int)b & 0xFF) << 8 | ((int)b & 0xFF);
            }
        }
    }

    public static void main(String[] args) {
        
        setupBuffer();
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
        for (int i = 0; i < 400000; i++) {
            if (i%1000==0) {
                raster.setDataElements(0, 0, width, height, buffer);
                panel.repaint();
                /*try { // un-comment this block fo code to save animation -- must have 'animation' directory
                    File outputFile = new File("animation/Crystal" + String.format("%04d",i/100) + ".png");
                    ImageIO.write(img, "png", outputFile);
                    System.out.println("Image Saved");
                }
                    catch (IOException e) {
                    System.out.println("Failed to save image");
                }*/
            }
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            while (true) {
                while(buffer[y*width+x] != 0) {
                    x = rand.nextInt(width);
                    y = rand.nextInt(height);
                }
                if (x==0 || y==0 || x==width-1 || y==height-1) {
                    break;
                }
                int r = rand.nextInt(8);
                if(r%2 == 0) { //random straight direction
                    if (x<width-1 && buffer[(y)*width+(x+1)]!=0 ||
                        x>0 && buffer[(y)*width+(x-1)]!=0 ||
                        y<height-1 && buffer[(y+1)*width+(x)]!=0 ||
                        y>0 && buffer[(y-1)*width+(x)]!=0) {
                        break;
                    }
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
                else { //random diagonal direction
                    if (x<width-1 && y<height-1 && buffer[(y+1)*width+(x+1)]!=0 ||
                        x>0 && y<height-1 && buffer[(y+1)*width+(x-1)]!=0 ||
                        x<width-1 && y>0 && buffer[(y-1)*width+(x+1)]!=0 ||
                        x>0 && y>0 && buffer[(y-1)*width+(x-1)]!=0) {
                        break;
                    }
                    r/=2;
                    x += r%2 == 0 ? 1:-1;
                    r/=2;
                    y += r%2 == 0 ? 1:-1;
                }
                x = (x+width)%width;
                y = (y+height)%height;
            }
            if (x>=0 && y>=0 && x<width && y<height)
                buffer[y*width + x] =
                    ((int)(255.0/(i/50000.0+1.0)))<<16 |
                    ((int)(255.0/(i/200000.0+1.0)))<<8 |
                    0xFF;
        }
        
        raster.setDataElements(0, 0, width, height, buffer);
        
        panel.repaint();

        System.out.println("finished!");
        try {
            File outputFile = new File("Crystal.png");
            ImageIO.write(img, "png", outputFile);
            System.out.println("Image Saved");
        }
        catch (IOException e) {
            System.out.println("Failed to save image");
        }

    }
}
