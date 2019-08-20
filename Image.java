/**
 * Created by 久弘 on 2017/1/5.
 */
import java.awt.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Image {
    public static void main(String args[]) {
        File file = new File("C:\\Users\\user\\Desktop\\Image\\Image\\a.jpg");

        try {
            BufferedImage RGB_img = ImageIO.read(file);
            int height = RGB_img.getHeight();
            int width = RGB_img.getWidth();

            int[] gray_img;
            gray_img = gray_fun(RGB_img);   //灰階
            negative_fun(gray_img, width, height); //負片

            int[] gamma1 = new int[width * height];
            int[] gamma2 = new int[width * height];
            int[] gamma3 = new int[width * height];
            gamma1 = gamma_fun(gray_img, width, height, 1.5);  //gamma > 1
            gamma2 = gamma_fun(gray_img, width, height, 1);    //對比拉開
            gamma3 = gamma_fun(gray_img, width, height, 0.5);  //gamma < 1

            int[] pepper = new int[width * height];
            pepper = pepper_fun(gamma3, width, height); // 胡椒鹽雜訊

            midden_fun(pepper, width, height); //中值濾波器
            pepper = pepper_fun(gamma3, width, height);

            binary_fun(gamma1, width, height); // 二值化

            mean_fun(pepper, width, height); //平均濾波器

            sobel_fun(gamma2, width, height); //sobel

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int[] gray_fun(BufferedImage image) {   //灰階
        int width = image.getWidth();
        int height = image.getHeight();
        int gray[] = new int[height * width];  //一維陣列

        int count = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color c = new Color(image.getRGB(j, i));
                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);
                Color newColor = new Color(red + green + blue, red + green + blue, red + green + blue);
                gray[count] = red + green + blue; //將每個pixel的RGB加總起來存為一個值
                image.setRGB(j, i, newColor.getRGB());
                count++; //存取的位子+1
            }
        }

        try {
            File output = new File("C:\\Users\\user\\Desktop\\Image\\Image\\gray.jpg");
            ImageIO.write(image, "jpg", output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return gray;
    }

    public static void negative_fun(int[] gray_img, int width, int height) //負片
    {
        int[] pixels = new int[width * height]; //宣告跟gray_img同大小的陣列
        for (int i = 0; i < width * height; i++) {
            int x = 255 - gray_img[i]; //255-每個pixel
            pixels[i] = (0xff000000 | x << 16 | x << 8 | x); //轉回RGB
        }

        BufferedImage negative_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  //image 轉 BufferedImage
        negative_image.setRGB(0, 0, width, height, pixels, 0, width); //公式

        try {
            File file_negative = new File("C:\\Users\\user\\Desktop\\Image\\Image\\negative.jpg");
            ImageIO.write(negative_image, "jpg", file_negative);
        } catch (Exception e) {
        }
    }

    public static int[] gamma_fun(int[] gray_img, int width, int height, double value) {
        int max = 0, min = 255, x;
        int gamma1[] = new int[width * height];
        int gamma255[] = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            if (gray_img[i] > max) {
                max = gray_img[i];
            }
            if (gray_img[i] < min) {
                min = gray_img[i];
            }
        }
        for (int i = 0; i < width * height; i++) {
            x = (int) (Math.pow((double) (gray_img[i] - min) / (max - min), value) * 255);
            gamma255[i] = x;
            gamma1[i] = (0xff000000 | x << 16 | x << 8 | x);

        }
        BufferedImage gamma_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  //image 轉 BufferedImage
        gamma_image.setRGB(0, 0, width, height, gamma1, 0, width); //公式

        try {
            File file_gamma = new File("C:\\Users\\user\\Desktop\\Image\\Image\\gamma" + value + ".jpg");
            ImageIO.write(gamma_image, "jpg", file_gamma);
        } catch (Exception e) {
        }
        return gamma255;
    }

    static int[] pepper_fun(int[] gamma3, int width, int height) {
        int pepper[] = new int[width * height];
        int pepper255[] = new int[width * height];
        int x;
        for (int i = 0; i < width * height; i++) {
            int a = (int) (Math.random() * 10);
            if (a == 1) {
                x = 255;
            } else if (a == 2) {
                x = 0;
            } else
                x = gamma3[i];
            pepper255[i] = x;
            pepper[i] = (0xff000000 | x << 16 | x << 8 | x);
        }
        BufferedImage pepper_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  //image 轉 BufferedImage
        pepper_image.setRGB(0, 0, width, height, pepper, 0, width); //公式

        try {
            File file_pepper = new File("C:\\Users\\user\\Desktop\\Image\\Image\\pepperandsalt.jpg");
            ImageIO.write(pepper_image, "jpg", file_pepper);
        } catch (Exception e) {
        }
        return pepper255;
    }

    static void binary_fun(int[] gamma1, int width, int height) {
        int binary[] = new int[width * height];
        int sum = 0, avg = 0, x;
        for (int i = 0; i < width * height; i++) {
            sum = sum + gamma1[i];
        }
        avg = sum / (width * height);
        for (int i = 0; i < width * height; i++) {
            if (gamma1[i] > avg) {
                x = 255;
            } else
                x = 0;
            binary[i] = (0xff000000 | x << 16 | x << 8 | x);
        }

        BufferedImage binary_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  //image 轉 BufferedImage
        binary_image.setRGB(0, 0, width, height, binary, 0, width); //公式

        try {
            File file_binary = new File("C:\\Users\\user\\Desktop\\Image\\Image\\binary.jpg");
            ImageIO.write(binary_image, "jpg", file_binary);
        } catch (Exception e) {
        }
    }

    static void midden_fun(int[] pepper, int width, int height) {
        int[] mid = new int[width * height];
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                int arr[] = new int[9];
                arr[0] = pepper[width * j + i - width - 1];
                arr[1] = pepper[width * j + i - width];
                arr[2] = pepper[width * j + i - width + 1];
                arr[3] = pepper[width * j + i - 1];
                arr[4] = pepper[width * j + i];
                arr[5] = pepper[width * j + i + 1];
                arr[6] = pepper[width * j + i + width - 1];
                arr[7] = pepper[width * j + i + width];
                arr[8] = pepper[width * j + i + width + 1];

                Arrays.sort(arr);
                pepper[width * j + i] = arr[4];

            }
        }
        for (int i = 0; i < width * height; i++) {
            mid[i] = (0xff000000 | pepper[i] << 16 | pepper[i] << 8 | pepper[i]);
        }

        BufferedImage mid_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  //image 轉 BufferedImage
        mid_image.setRGB(0, 0, width, height, mid, 0, width); //公式

        try {
            File file_mid = new File("C:\\Users\\user\\Desktop\\Image\\Image\\mid.jpg");
            ImageIO.write(mid_image, "jpg", file_mid);
        } catch (Exception e) {
        }
    }

    static void mean_fun(int[] pepper, int width, int height) {
        int[] mean = new int[width * height];
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                int arr[] = new int[9];
                arr[0] = pepper[width * j + i - width - 1];
                arr[1] = pepper[width * j + i - width];
                arr[2] = pepper[width * j + i - width + 1];
                arr[3] = pepper[width * j + i - 1];
                arr[4] = pepper[width * j + i];
                arr[5] = pepper[width * j + i + 1];
                arr[6] = pepper[width * j + i + width - 1];
                arr[7] = pepper[width * j + i + width];
                arr[8] = pepper[width * j + i + width + 1];

                int x = (arr[0] + arr[1] + arr[2] + arr[3] + arr[4] + arr[5] + arr[6] + arr[7] + arr[8]) / 9;
                pepper[width * j + i] = x;
            }
        }
        for (int i = 0; i < width * height; i++) {
            mean[i] = (0xff000000 | pepper[i] << 16 | pepper[i] << 8 | pepper[i]);
        }

        BufferedImage mean_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  //image 轉 BufferedImage
        mean_image.setRGB(0, 0, width, height, mean, 0, width); //公式

        try {
            File file_mean = new File("C:\\Users\\user\\Desktop\\Image\\Image\\mean.jpg");
            ImageIO.write(mean_image, "jpg", file_mean);
        } catch (Exception e) {
        }
    }

    static void sobel_fun(int[] gamma2, int width, int height) {
        int[] sobel = new int[width * height];
        int[] neww = new int[width*height];
        double n;

        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                int arr[] = new int[9];
                int x = 0, y = 0;
                arr[0] = gamma2[width * j + i - width - 1];
                arr[1] = gamma2[width * j + i - width];
                arr[2] = gamma2[width * j + i - width + 1];
                arr[3] = gamma2[width * j + i - 1];
                arr[4] = gamma2[width * j + i];
                arr[5] = gamma2[width * j + i + 1];
                arr[6] = gamma2[width * j + i + width - 1];
                arr[7] = gamma2[width * j + i + width];
                arr[8] = gamma2[width * j + i + width + 1];
                x = (arr[0] * (-1)) + arr[2] - (arr[3] * 2) + (arr[5] * 2) - arr[6]  + arr[8];
                y = arr[0] + (arr[1] * 2) + arr[2]  - arr[6]  - (arr[7] *2) - arr[8];
                n = Math.sqrt(x*x+y*y);
                if (n > 255)
                    n = 255;
                neww[width * j + i] = (int) n;
            }
        }
        for (int i = 0; i < width*height; i++) {
            sobel[i] = (0xff000000 | neww[i] << 16 | neww[i] << 8 |neww[i]);
        }

        BufferedImage sobel_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);  //image 轉 BufferedImage
        sobel_image.setRGB(0, 0, width, height, sobel, 0, width); //公式

        try {
            File file_mean = new File("C:\\Users\\user\\Desktop\\Image\\Image\\sobel.jpg");
            ImageIO.write(sobel_image, "jpg", file_mean);
        } catch (Exception e) {
        }
    }
}
