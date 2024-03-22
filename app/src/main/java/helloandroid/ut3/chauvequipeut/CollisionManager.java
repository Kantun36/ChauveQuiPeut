package helloandroid.ut3.chauvequipeut;

import android.graphics.PointF;

public class CollisionManager {


    public static boolean checkForCollision(int[][] bat, float[][] ob) {
        boolean toReturn = intersects(new PointF(bat[0][0],bat[0][1]), new PointF(bat[1][0],bat[1][1]), // verif collision haut bat avec gauche obstacle
                new PointF(ob[0][0],ob[0][1]), new PointF(ob[2][0],ob[2][1])) ||

                intersects(new PointF(bat[1][0],bat[1][1]), new PointF(bat[3][0],bat[3][1]),// verif collision droite bat avec gauche obstacle
                        new PointF(ob[0][0],ob[0][1]), new PointF(ob[2][0],ob[2][1])) ||

                intersects(new PointF(bat[0][0],bat[0][1]), new PointF(bat[2][0],bat[2][1]),// verif collision gauche bat avec droite obstacle
                        new PointF(ob[1][0],ob[1][1]), new PointF(ob[2][0],ob[2][1])) ||

                intersects(new PointF(bat[2][0],bat[2][1]), new PointF(bat[3][0],bat[3][1]),// verif collision bas bat avec droite obstacle
                        new PointF(ob[1][0],ob[1][1]), new PointF(ob[2][0],ob[2][1]));

        return toReturn;
    }
    static boolean intersects(PointF a1, PointF a2, PointF b1, PointF b2) {
        PointF intersection = new PointF();

        PointF b = new PointF(a2.x - a1.x, a2.y - a1.y);
        PointF d = new PointF(b2.x - b1.x, b2.y - b1.y);
        float bDotDPerp = b.x * d.y - b.y * d.x;

        // if b dot d == 0, it means the lines are parallel so have infinite intersection points
        if (bDotDPerp == 0)
            return false;

        PointF c = new PointF(b1.x - a1.x, b1.y - a1.y);
        float t = (c.x * d.y - c.y * d.x) / bDotDPerp;
        if (t < 0 || t > 1)
            return false;

        float u = (c.x * b.y - c.y * b.x) / bDotDPerp;
        if (u < 0 || u > 1)
            return false;

        intersection.set(a1.x + b.x * t, a1.y + b.y * t);

        return true;
    }

}
