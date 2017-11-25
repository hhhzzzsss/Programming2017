import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Color;
public class RayTracer {
    public static final int width = 1920;
    public static final int height = 1080;
    public static final int alias = 2;
    public static final float clipDist = 0.001f;
    public static final float maxBounce = 5;
    public static final float gamma = 2.2f;
    public static final float invgamma = 1.0f/gamma;

    public static float[] rotmat;
    public static float[] sundir;
    public static float[] suncol;
    public static float[] ambient;

    public static final float[] BLACK = new float[] {0.0f, 0.0f, 0.0f};
    public static final float[] WHITE = new float[] {1.0f, 1.0f, 1.0f};

    public static void main(String[] args) {

        JFrame frame = new JFrame("Ray Tracer by Harry Zhou");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = img.getRaster();
        JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(img, 0, 0, null);
                }
            };

        panel.setPreferredSize(new Dimension(width,height));
        frame.add(panel);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        initScene();
        drawImage(raster);
        //img.createGraphics().fillRect(0,0,200,200);
        panel.repaint();
        try {
            File outputFile = new File("RayTrace.png");
            ImageIO.write(img, "png", outputFile);
            System.out.println("Image saved!");
        }
        catch (IOException e) {
            System.out.println("Failed to save image");
        }
    }
    
    static float[] vadd(float[] v1, float[]v2) {  return new float[] { v1[0]+v2[0], v1[1]+v2[1], v1[2]+v2[2] };  }
    static float[] vsub(float[] v1, float[]v2) {  return new float[] { v1[0]-v2[0], v1[1]-v2[1], v1[2]-v2[2] };  }
    static float[] vmul(float[] v1, float[]v2) {  return new float[] { v1[0]*v2[0], v1[1]*v2[1], v1[2]*v2[2] };  }
    static float[] vdiv(float[] v1, float[]v2) {  return new float[] { v1[0]/v2[0], v1[1]/v2[1], v1[2]/v2[2] };  }
    static float[] vsmul(float[] v1, float f) {  return new float[] { v1[0]*f, v1[1]*f, v1[2]*f };  }
    static float[] vsdiv(float[] v1, float f) {  return new float[] { v1[0]/f, v1[1]/f, v1[2]/f };  }
    static float[] vspow(float[] v1, float f) {  return new float[] { (float)Math.pow(v1[0],f), (float)Math.pow(v1[1],f), (float)Math.pow(v1[2],f) };  }
    static float[] vneg(float[] v) { return new float[] {-v[0], -v[1], -v[2]}; };
    static float vdot(float[] v1, float[] v2) {  return v1[0]*v2[0] + v1[1]*v2[1] + v1[2]*v2[2];  }
    static float vlen(float[] v) {  return (float) Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]);  }
    static float[] normalize(float[] v) { return vsdiv(v, vlen(v)); }
    static float[] vmix(float[] v1, float[] v2, float a) {  return vadd( v1, vsmul(vsub(v2,v1), a) );  }
    static float[] vmatmultiply(float[] mat, float[] v) {  return new float[] { v[0]*mat[0]+v[1]*mat[1]+v[2]*mat[2], v[0]*mat[3]+v[1]*mat[4]+v[2]*mat[5], v[0]*mat[6]+v[1]*mat[7]+v[2]*mat[8]  };  }
    
    static void initScene() {
        float theta = -0.2f;
        rotmat = new float[] {
            1.0f, 0.0f,                    0.0f,
            0.0f, (float)Math.cos(theta),  (float)Math.sin(theta),
            0.0f, (float)-Math.sin(theta), (float)Math.cos(theta)
        };
        sundir = normalize(new float[] {-1.0f,1.0f,-1.0f});
        suncol = new float[] {1.0f, 0.95f, 0.85f};
        ambient = new float[] {0.15f, 0.15f, 0.2f};
    }

    static float[] getDiffuse(float[] nor, float[] lig, float[] ligcolor, float[] objcolor) {
        float dif = vdot(nor, lig);
        if (dif <= 0.0f) {
            return BLACK;
        }
        return vmul(vsmul(ligcolor, dif), objcolor);
    }
    
    static float[] getSpecular(float[] nor, float[] rd, float[] lig, float[] ligcolor, float shininess) {
        float tc = vdot(nor, rd);
        if (tc >= 0.0f) {
            return BLACK;
        }
        float[] tvc = vsmul(nor, tc);
        float[] n_rd = vsub(rd, vadd(tvc, tvc));
        float spec = vdot(n_rd, lig);
        if (spec <= 0.0f) {
            return BLACK;
        }
        return vsmul(ligcolor, (float)Math.pow(spec, shininess));
    }

    static float[] getReflection(float[] pos, float[] nor, float[] rd, int bounce) {
        float tc = vdot(nor, rd);
        if (tc > 0.0f) {
            tc = -tc;
            nor = vneg(nor);
        }
        float[] tvc = vsmul(nor, tc);
        float[] n_rd = vsub(rd, vadd(tvc, tvc));
        return drawRay(pos, n_rd, bounce+1, 0);
    }

    static float getFresnel(float n1, float n2, float[] nor, float[] rd) {
        float r0 = (n1-n2)/(n1+n2);
        r0 *= r0;
        float cosT = -vdot(nor, rd);
        if (n1 > n2) {
            float r = n1/n2;
            float sinT2 = r*r*(1.0f-cosT*cosT);
            if (sinT2 > 1.0f) return 1.0f;
            cosT = (float) Math.sqrt(1.0f-sinT2);
        }
        float x = 1.0f-cosT;
        return r0 + (1.0f-r0)*x*x*x*x*x;
    }

    static float[] getDielectric(float[] pos, float[] nor, float[] rd, float[] lig, float[] ligcolor, float[] objcolor, float shininess, float reflectivity, float difamt, float specamt, int bounce) {
        float[] dif = getDiffuse(nor, lig, ligcolor, objcolor);
        float[] spec = getSpecular(nor, rd, lig, ligcolor, shininess);
        float[] shadow = drawRay(pos, lig, bounce+1, 1);
        dif = vsmul(dif, difamt);
        spec = vsmul(spec, specamt);
        float[] phong = vmul(vadd(dif, spec), shadow);

        float[] reflection = getReflection(pos, nor, rd, bounce);
        reflectivity = reflectivity + (1.0f-reflectivity)*getFresnel(1.0f, 1.45f, nor, rd)*0.5f;
        
        float[] color = vmix(phong, reflection, reflectivity);
        
        return color;
    }

    static float[] getTranslucent(float[] pos, float[] nor, float[] rd, float n1, float n2, float[] absorption, int bounce) {
        boolean rayEntering = true; //whether or not the ray is entering the material.

        float[] refraction = BLACK;
        boolean TotalInternalReflection = false;
        float r = n1/n2;
        float cosT1 = -vdot(nor, rd);
        if (cosT1 < 0.0f) { //Normal pointing in wrong direction, thus ray must be exiting.
            rayEntering = false;
            cosT1 = -cosT1;
            nor = vneg(nor);
            float tn = n1;
            n1 = n2;
            n2 = tn;
            r = n1/n2;
        }
        float sinT1 = (float) Math.sqrt(1.0f - cosT1*cosT1);
        float sinT2 = r * sinT1;
        float[] n_rd;
        if (sinT2 < 1.0f) {
            float cosT2 = (float) Math.sqrt(1.0f - sinT2*sinT2);
            n_rd = vadd( vsmul(rd,r) , vsmul(nor, r*cosT1 - cosT2) );
            if (rayEntering) refraction = drawRay(pos, n_rd, bounce+1, 2);
            else refraction = drawRay(pos, n_rd, bounce+1, 0);
        }
        else {
            TotalInternalReflection = true;
        }

        float[] reflection;
        float tc = vdot(nor, rd);
        float[] tvc = vsmul(nor, tc);
        n_rd = vsub(rd, vadd(tvc, tvc));
        if (!rayEntering) reflection = drawRay(pos, n_rd, bounce+1, 2);
        else reflection = drawRay(pos, n_rd, bounce+1, 0);
        
        if (rayEntering) {
            if (!TotalInternalReflection && refraction[3] > 0.0f)
                refraction = vmul( refraction, vspow(absorption, refraction[3]) );
        }
        else {
            if (reflection[3] > 0.0f)
                reflection = vmul( reflection, vspow(absorption, reflection[3]) );
        }
        if (TotalInternalReflection) return reflection;
        float reflectivity = getFresnel(n1, n2, nor, rd);
        return vmix(refraction, reflection, reflectivity);
    }

    static float[] sphereIntersect(float[] c, float radius, float[] ro, float[] rd) {
        float[] L = vsub(c, ro);
        float tca = vdot(L, rd);
        //if (tca < 0.0f) return null;
        float d = (float) Math.sqrt( vdot(L,L) - tca*tca );
        if (d>radius) return null;
        float thc = (float) Math.sqrt(radius*radius - d*d);
        float t0 = tca-thc;
        float t1 = tca+thc;

        float[] intersect = new float[4];
        if (t0 < 0.001f) {
            if (t1 < 0.001f) return null;
            intersect[0] = t1;//vadd(ro, vsmul(rd,t1));
        }
        else if (t1 < 0.001f) intersect[0] = t0;//vadd(ro, vsmul(rd,t0));
        else if (t0 < t1)  intersect[0] = t0;//vadd(ro, vsmul(rd,t0));
        else intersect[0] = t1;//vadd(ro, vsmul(rd,t1));

        float[] R = vsub(vadd(ro, vsmul(rd, intersect[0])), c);
        float[] normal = normalize(R);
        intersect[1] = normal[0];
        intersect[2] = normal[1];
        intersect[3] = normal[2];
        return intersect;
    }

    static float[] floorIntersect(float y, float[] ro, float[] rd) {
        if (Math.abs(rd[1]) < 0.0001f) return null;
        float t = (y-ro[1]) / rd[1];
        if (t < 0.001f) return null;
        else if (rd[1] < 0.0f) return new float[] {t, 0.0f, 1.0f, 0.0f};
        else return new float[] {t, 0.0f, -1.0f, 0.0f};
    }
    
    static boolean closerIntersect(float[] intersect, float[] newintersect) {
        if (newintersect == null) return false;
        if (intersect == null) return true;
        if (intersect[0] < newintersect[0]) return false;
        return true;
    }

    static float[] drawRay(float[] ro, float[] rd, int bounce, int rayType) { // intersection array is [dist, normalx, y, z] // rayType: 0=default 1=shadow 2=volumetric
        if (bounce > maxBounce) {
            if (rayType == 2) return new float[] {0.0f, 0.0f, 0.0f, 0.0f};
            return BLACK;
        }
        float[] intersect = null;
        int objectID = -1;

        float[] floor = floorIntersect(0.0f, ro, rd);
        if (floor == null || Math.abs(floor[0]) > 20.0f) {

            floor = null;
        }
        if (closerIntersect(intersect, floor)) {
            intersect = floor;
            objectID = 0;
        }

        float[] lens = sphereIntersect(new float[] {0.0f,1.0f,0.0f}, 1.0f, ro, rd);
        if (closerIntersect(intersect, lens)) {
            intersect = lens;
            objectID = 1;
        }

        float[] sph = sphereIntersect(new float[] {3.0f,1.0f,2.5f}, 1.0f, ro, rd);
        if (closerIntersect(intersect, sph)) {
            intersect = sph;
            objectID = 2;
        }

        float[] mirror = sphereIntersect(new float[] {-2.5f,1.0f,1.0f}, 1.0f, ro, rd);
        if (closerIntersect(intersect, mirror)) {
            intersect = mirror;
            objectID = 3;
        }

        if (rayType == 1) { // shadow ray
            if (intersect == null) return WHITE;
            else return BLACK;
        }
        
        float[] color = BLACK;
        float[] pos = null;
        float[] nor = null;
        if (intersect != null) {
            pos = vadd(ro, vsmul(rd, intersect[0]));
            nor = new float[] {intersect[1], intersect[2], intersect[3]};
            color = BLACK;
        }
        if (intersect == null) {
            float[] bottom = WHITE;
            float[] zenith = new float[] {0.3f, 0.7f, 1.0f};
            color = vmix(bottom, zenith, rd[1]/2.0f + 0.5f);
        }
        else if (objectID == 0) {
            float[] objcol;
            if (((int)Math.floor(pos[0]) + (int)Math.floor(pos[2])) % 2 == 0) {
                objcol = WHITE;
            }
            else {
                objcol = new float[] {0.0f, 0.0f, 0.0f};
            }
            float[] dielectric = getDielectric(pos, nor, rd, sundir, suncol, objcol, 5.0f, 0.1f, 0.8f, 0.0f, bounce);
            color = vadd(color, dielectric);
            color = vadd(color, vmul(ambient, objcol));
        }
        else if (objectID == 1) {
            float[] objcol = new float[] {0.4f, 0.9f, 0.9f};
            float[] translucent = getTranslucent(pos, nor, rd, 1.0f, 1.45f, new float[] {0.2f, 0.6f, 0.8f}, bounce);
            color = vadd(color, translucent);
        }
        else if (objectID == 2) {
            float[] objcol = new float[] {0.3f, 0.9f, 0.6f};
            float[] dielectric = getDielectric(pos, nor, rd, sundir, suncol, objcol, 10.0f, 0.0f, 0.7f, 0.4f, bounce);
            color = vadd(color, dielectric);
            color = vadd(color, vmul(ambient, objcol));
        }
        else if (objectID == 3) {
            float[] objcol = new float[] {0.8f, 0.4f, 0.2f};
            float[] reflection = getReflection(pos, nor, rd, bounce);
            color = vmul(vadd(color, reflection), objcol);
        }

        if (color[0] > 1.0f) {
            color[0] = 1.0f;
        }
        if (color[1] > 1.0f) {
            color[1] = 1.0f;
        }
        if (color[2] > 1.0f) {
            color[2] = 1.0f;
        }

        if (rayType == 2) { // volumetric ray
            if (intersect == null) {
                color = new float[] {color[0], color[1], color[2], 0.0f};
            }
            else {
                color = new float[] {color[0], color[1], color[2], intersect[0]};
            }
        }

        return color;
    }
    
    static float[] camera(float x, float y) { // optimally from a range of -1.0 to 1.0 for both x and y
        float[] ro = {-0.2f, 1.5f, -3.0f};
        float[] rd = normalize(new float[] {x, y, 1.5f});
        rd = vmatmultiply(rotmat, rd);

        float[] color = drawRay(ro, rd, 0, 0);
        return vspow(color, invgamma);
    }

    static void drawImage(WritableRaster r) {
        int[] pixels = new int[width*height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float u = (float)x/width * 2.0f - 1.0f;
                float v = (float)y/height * 2.0f - 1.0f;
                u = u * width / height;
                v = -v;
                
                float[] color = BLACK;
                float aliasoffset = 1.0f/height / alias;
                for (int ax = 0; ax < alias; ax++) {
                    for (int ay = 0; ay < alias; ay++) {
                        color = vadd(color, camera(u+ax*aliasoffset, v+ay*aliasoffset));
                    }
                }
                color = vsdiv(color, alias*alias);

                pixels[(y*width+x)] =
                    ((int) (0xff*color[0])) << 16 |
                    ((int) (0xff*color[1])) << 8 |
                    ((int) (0xff*color[2]));
            }
        }
        r.setDataElements(0,0,width,height,pixels);
    }


}
