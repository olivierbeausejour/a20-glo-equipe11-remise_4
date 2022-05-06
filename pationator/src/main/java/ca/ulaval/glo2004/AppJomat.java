package ca.ulaval.glo2004;


import ca.ulaval.glo2004.utils.Dimensions;
import ca.ulaval.glo2004.utils.Vector3;

public class AppJomat {

    static Vector3 pos1;
    static Vector3 pos2;
    static Vector3 pos3;

    public static void main(String[] args) {
        pos1 = new Vector3(0f, 0f, 0f);
        pos2 = new Vector3(19f, 38f, 2438.4f);
        pos3 = new Vector3(38f, 140f, 2438.4f);

        System.out.println("pos  = " + pos1.x + " - " + pos1.y + " - " + pos1.z);
        System.out.println("pos2 = " + pos2.x + " - " + pos2.y + " - " + pos2.z);

        Dimensions dim1 = new Dimensions(pos1);
        Dimensions dim2 = new Dimensions(pos2);
        Dimensions dim3 = new Dimensions(pos3);

        System.out.println();

        //System.out.println("dim1  = " + dim1.getMetricWidth() + " - " + dim1.getMetricHeight() + " - " + dim1.getMetricDepth());
        System.out.println("dim1  = " + dim1.getActualWidth() + " - " + dim1.getActualHeight() + " - " + dim1.getActualDepth());
        System.out.println("dim1  = " + dim1.getNominalWidth() + " - " + dim1.getNominalHeight() + " - " + dim1.getNominalDepth());

        System.out.println();

        //System.out.println("dim2  = " + dim2.getMetricWidth() + " - " + dim2.getMetricHeight() + " - " + dim2.getMetricDepth());
        System.out.println("dim2  = " + dim2.getActualWidth() + " - " + dim2.getActualHeight() + " - " + dim2.getActualDepth());
        System.out.println("dim2  = " + dim2.getNominalWidth() + " - " + dim2.getNominalHeight() + " - " + dim2.getNominalDepth());

        System.out.println();

        //System.out.println("dim3  = " + dim3.getMetricWidth() + " - " + dim3.getMetricHeight() + " - " + dim3.getMetricDepth());
        System.out.println("dim3  = " + dim3.getActualWidth() + " - " + dim3.getActualHeight() + " - " + dim3.getActualDepth());
        System.out.println("dim3  = " + dim3.getNominalWidth() + " - " + dim3.getNominalHeight() + " - " + dim3.getNominalDepth());
    }
}
