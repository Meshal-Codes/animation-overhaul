package net.lizistired.animationoverhaul.util.math;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class RotationMatrix {
    public float[][] matrixGrid;

    public RotationMatrix(float[][] matrixGrid){
        this.matrixGrid = matrixGrid;
    }

    public Vec3f toEulerAngles(){
        float x = (float) -Math.asin(this.matrixGrid[2][0]);
        float y = (float) Math.atan2(this.matrixGrid[1][0], this.matrixGrid[0][0]);
        float z = (float) Math.atan2(this.matrixGrid[2][1], this.matrixGrid[2][2]);
        return new Vec3f(y, x, z);
    }

    public static RotationMatrix fromEulerAngles(Vec3f anglesRadian){
        float w = anglesRadian.getX(); // w yaw x y x
        float v = anglesRadian.getY(); // v pitch y x y
        float u = anglesRadian.getZ(); // u roll z z z

        float su = MathHelper.sin(u);
        float cu = MathHelper.cos(u);
        float sv = MathHelper.sin(v);
        float cv = MathHelper.cos(v);
        float sw = MathHelper.sin(w);
        float cw = MathHelper.cos(w);

        float[][] grid = new float[][]{
                {
                    cv * cw,
                    (su * sv * cw) - (cu * sw),
                    (su * sw) + (cu * sv * cw)
                },
                {
                    cv * sw,
                    (cu * cw) + (su * sv * sw),
                    (cu * sv * sw) - (su * cw)
                },
                {
                    -sv,
                    su * cv,
                    cu * cv
                }
        };
        return new RotationMatrix(grid);
    }

    public static RotationMatrix getInverse(RotationMatrix rotationMatrix){
        float[][] originalGrid = rotationMatrix.matrixGrid;
        float[][] newGrid = blankGrid();

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                newGrid[i][j] = originalGrid[j][i];
            }
        }
        return new RotationMatrix(newGrid);
    }

    public void mult(RotationMatrix secondMatrix){
        float[][] firstGrid = this.matrixGrid;
        float[][] secondGrid = secondMatrix.matrixGrid;
        float[][] finalGrid = blankGrid();

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                finalGrid[i][j] = 0;
                for(int k = 0; k < 3; k++){
                    finalGrid[i][j] += firstGrid[i][k] * secondGrid[k][j];
                }
            }
        }
        this.matrixGrid = finalGrid;
    }

    public static float[][] blankGrid(){
        return new float[][]{
                {0, 0, 0},
                {0, 0, 0},
                {0, 0, 0}
        };
    }

    private static float c(float x){
        return MathHelper.cos(x);
    }

    private static float s(float x){
        return MathHelper.sin(x);
    }
}
