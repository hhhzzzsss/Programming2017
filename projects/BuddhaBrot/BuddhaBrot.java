import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferByte;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Color;
import java.util.SplittableRandom;
public class BuddhaBrot {
    static final int width = 800;
    static final int height = 800;
    static final int maxIterations = 1000;
    static final int samples = 40000000;
    static final int colorDiv = 700;

    public static BufferedImage initImg;
    public static int initImgWidth;
    public static int initImgHeight;
    public static byte[] initImgArray;
    public static int[] finImgArray;
    public static WritableRaster finImgRaster;
    public static BufferedImage img;

    public static SplittableRandom rand = new SplittableRandom();
    
    public static void main(String[] args) {
        JFrame frame = new JFrame("Buddhabrot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        finImgRaster = img.getRaster();
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
        init();
        drawFractal();
        panel.repaint();
        System.out.println("finished!");
        try {
            File outputFile = new File("Nebula.png");
            ImageIO.write(img, "png", outputFile);
            System.out.println("Image Saved");
        }
        catch (IOException e) {
            System.out.println("Failed to save image");
        }
    }

    public static void init() {
        try {
            initImg = ImageIO.read(new File("PolarBear.jpg"));
        }
        catch (IOException e) {
            System.out.println("Failed to read file");
            System.exit(0);
        }
        initImgArray = ((DataBufferByte)initImg.getRaster().getDataBuffer()).getData();
        initImgWidth = initImg.getWidth();
        initImgHeight = initImg.getHeight();
        finImgArray = new int[3*width*height];
    }

    public static void drawFractal() {
        for (int i = 0; i < samples; i++) {
            double x = rand.nextDouble(-2.0, 2.0);
            double y = rand.nextDouble(-2.0, 2.0);
            iteratePoint(x, y);
            if (i % 100000 == 0) {
                System.out.println("Sample " + i + "/" + samples);
            }
        }
        int[] pixels = new int[width*height];
        for (int i = 0; i < 3*width*height; i++) {
            finImgArray[i] = Math.min(255, finImgArray[i]/colorDiv);
        }
        for (int i = 0; i < width*height; i++) {
            pixels[i] = 
                ((int) (0xff & finImgArray[3*i+2])) << 16 |
                ((int) (0xff & finImgArray[3*i+1])) << 8 |
                ((int) (0xff & finImgArray[3*i]));
        }
        finImgRaster.setDataElements(0,0,width,height,pixels);
    }

    public static void iteratePoint(double x, double y) {
        int[] imgColors = texture((x+2.0)*3.0%2.0 - 1.0, (y+2.0)*3.0%2.0 - 1.0);
        int[] orbit = new int[maxIterations];
        double real = x;
        double imag = y;
        boolean escaped = false;
        int iterations = maxIterations;
        for (int i = 0; i < maxIterations; i++) {
            double ti = real;
            double tr = imag;
            real = tr*tr - ti*ti + x;
            imag = -2.0*tr*ti + y;
            if (real*real + imag*imag > 8.0) {
                escaped = true;
                iterations = i;
                break;
            }
            int xpix = (int)((real+2.0) / 4.0 * width);
            int ypix = (int)((imag+2.0) / 4.0 * height);
            if (xpix >= 0 && ypix >= 0 && xpix < width && ypix < height) {
                orbit[i] = 3*(ypix*width+xpix);
            }
            else {
                orbit[i] = -1;
            }
        }
        if (escaped) {
            for (int i = 0; i < iterations; i++) {
                if(orbit[i] != -1) {
                    finImgArray[orbit[i]] += 160 + (int)(imgColors[0]*(95.0/255.0));
                    finImgArray[orbit[i]+1] += 200;
                    finImgArray[orbit[i]+2] += imgColors[2];
                }
            }
        }
    }

    public static int[] texture(double x, double y) {
        double imgx = (x+1.0)/2.0 * initImgWidth;
        double imgy = (y+1.0)/2.0 * initImgHeight;
        double flx = Math.floor(imgx);
        double fly = Math.floor(imgy);
        int index = (int)fly * initImgWidth + (int)flx;
        double ax = imgx - flx;
        double ay = imgy - fly;
        
        int[] color = new int[3];
        for (int i = 0; i < 3; i++) {
            int x1 = initImgArray[(index) * 3 + i] & 0xff;
            int x2;
            double y1;
            if (flx < initImgWidth-1) {
                x2 = initImgArray[(index+1) * 3 + i] & 0xff;
                y1 = ( x1 + (x2-x1) * ax );
            }
            else {
                y1 = x1;
            }
            double y2;
            if (fly < initImgHeight-1) {
                x1 = initImgArray[(index+initImgWidth) * 3 + i] & 0xff;
                if (flx < initImgWidth-1) {
                    x2 = initImgArray[(index+initImgWidth+1) * 3 + i] & 0xff;
                    y2 = ( x1 + (x2-x1) * ax );
                }
                else {
                    y2 = x1;
                }
                color[i] = (int) ( y1 + (y2-y1) * ay );
            }
            else {
                color[i] = (int) y1;
            }
            color[i] = color[i] & 0xff;
        }

        return color;
    }

}
