package hu.codeandsoda.osa.myshoebox.data;


public enum Rotation {

    ORIGINAL(360),
    THREE_QUARTERS(270),
    HALF(180),
    ONE_QUARTER(90);
    
    private int rotationValue;

    private Rotation(int rotationValue) {
        this.rotationValue = rotationValue;
    }

    public int getRotationValue() {
        return rotationValue;
    }

    public static boolean isRotationValueValid(int rotationValue) {
        boolean validRotationValue = false;
        if (rotationValue >= 0) {
            for (Rotation validRotation : Rotation.values()) {
                if (rotationValue % validRotation.getRotationValue() == 0) {
                    validRotationValue = true;
                    break;
                }
            }
        }
        return validRotationValue;
    }

}
