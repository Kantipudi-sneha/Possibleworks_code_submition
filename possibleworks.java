import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileReader;
import java.util.*;
import java.math.BigInteger;

public class possibleworks {
    public static void main(String[] args) {
        try {
            // Load JSON file
            JSONObject jsonObject = new JSONObject(new JSONTokener(new FileReader("possible.json")));
            
            // Read n and k values
            JSONObject keys = jsonObject.getJSONObject("keys");
            int n = keys.getInt("n");
            int k = keys.getInt("k");

            // Extract (x, y) pairs
            List<int[]> points = new ArrayList<>();
            for (String key : jsonObject.keySet()) {
                if (!key.equals("keys")) {
                    int x = Integer.parseInt(key);
                    JSONObject valueObj = jsonObject.getJSONObject(key);
                    int base = valueObj.getInt("base");
                    String encodedValue = valueObj.getString("value");
                    
                    // Convert the encoded value from the specified base to decimal
                    BigInteger y = decodeValue(base, encodedValue);
                    points.add(new int[]{x, y.intValue()}); // Store as int for x, but y can be BigInteger
                }
            }

            // Select k points (if more than required)
            points.sort(Comparator.comparingInt(a -> a[0]));
            List<int[]> selectedPoints = points.subList(0, k);

            // Compute constant term using Lagrange interpolation
            BigInteger c = lagrangeInterpolation(selectedPoints);
            System.out.println("Constant (c): " + c);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Decode the value from the specified base to decimal
    private static BigInteger decodeValue(int base, String value) {
        // Convert the value to uppercase to handle bases above 10
        value = value.toUpperCase();
        return new BigInteger(value, base);
    }

    // Lagrange Interpolation to find f(0) (constant term)
    private static BigInteger lagrangeInterpolation(List<int[]> points) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < points.size(); i++) {
            BigInteger term = BigInteger.valueOf(points.get(i)[1]); // Start with y_i

            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    term = term.multiply(BigInteger.valueOf(0 - points.get(j)[0]))
                               .divide(BigInteger.valueOf(points.get(i)[0] - points.get(j)[0]));
                }
            }

            result = result.add(term);
        }
        
        return result; // Return as BigInteger
    }
}
