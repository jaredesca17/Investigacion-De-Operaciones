import java.util.scanner;
public class Simplex {
    private double [] [] tabla; // Tabla simplex
    private int numVariables; // Número de variables de decisión
    private int numRestricciones; // Numero de restricciones
    private int numVariablesTotales; // Variables + holguras
    private boolean esMaximizacion;
    private int [] variablesBasicas;
    private String [] nombresVariables;
    public static void main (String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== MÉTODO SIMPLEX ===");
        System.out.print("¿Es maximización (1) o minimización (2)?: ");
        int tipo = scanner.nextInt();
        boolean esMaximizacion = (tipo == 1);
        System.out.print("Número de variables de decisión (x1, x2, ...): ");
        int numVariables = scanner.nextInt();
        System.out.print("Número de restricciones: ");
        int numRestricciones = scanner.nextInt();
        MetodoSimplex simplex = new MetodoSimplex(numVariables, numRestricciones, esMaximizacion);
        simplex.ingresarDatos(scanner);
        simplex.resolver();
        scanner.close();
    }
    public Simplex (int numVariables, int NumRestricciones, boolean esMaximizacion) {
        this.numVariables = numVariables;
        this.numRestricciones =numRestricciones;
        this.esMaximizacion = esMaximizacion;
        this.numVariablesTotales = numVariables + numRestricciones;
        this.variableBasicas = new int [numRestricciones];
        this.nombresVariables = new int [numVariablesTotales];
        // Inicializar nombres de variables
        for (int i = 0; i < numRestricciones; i++) {
            nombresVariables [numVariables + 1] = "s" + (i + 1); 
        }
        for (int i = 0; i < numRestricciones; i++) {
            nombresVariables[numVariables + i] = "s" + (i + 1);
        }
        // Inicializar tabla simplex
        tabla = new double[numRestricciones + 1][numVariablesTotales + 1];
    }
    public void ingresarDatos(Scanner scanner) {
        System.out.println("\n=== INGRESO DE LA FUNCIÓN OBJETIVO ===");
        System.out.println("Ingrese los coeficientes de la función objetivo:");
        // Ingresar coeficientes de la función objetivo para variables de decisión
        for (int j = 0; j < numVariables; j++) {
            System.out.printf("Coeficiente de x%d: ", j + 1);
            double coeficiente = scanner.nextDouble();
            if (esMaximizacion) {
                tabla[numRestricciones][j] = -coeficiente; // Maximización: Z positiva, demás negativos
            } else {
                tabla[numRestricciones][j] = coeficiente; // Minimización: Z negativa, demás positivos
            }
        }
        // Coeficientes de las variables de holgura en la funcion objetivo = 1
        for (int j = numVariables; j < numVariablesTotales; j++) {
            tabla [numRestricciones] [j] = 1.0;
         }
         System.out.println("\n=== INGRESO DE RESTRICCIONES ===");
         for (int i = 0; 1 < numRestricciones; i++) {
             System.out.println("\n--- Restricion %d ---\n", i + 1);
            // Coeficientes de las variables de decisión
            for (int j = 0; j < numVariables; j++) {
                System.out.printf("Coeficiente de x%d: ", j + 1);
                tabla[i][j] = scanner.nextDouble();
            }
            // Coeficientes de las variables de holgura
            for (int j = numVariables; j < numVariablesTotales; j++) {
                tabla[i][j] = (j == numVariables + i) ? 1.0 : 0.0;
            }
            // Lado derecho (LD)
            System.out.print("Lado derecho (LD) de la restricción: ");
            tabla[i][numVariablesTotales] = scanner.nextDouble();
            variablesBasicas[i] = numVariables + i; // Variable de holgura básica inicial
        }
        // Mostrar tabla inicial
        System.out.println("\n=== TABLA INICIAL SIMPLEX ===");
        imprimirTabla();
    }
    public void resolver() {
        System.out.println("\n=== INICIO DEL MÉTODO SIMPLEX ===");
        int iteracion = 0;
        while (!esOptimo()) {
            iteracion++;
            System.out.printf("\n--- Iteración %d ---\n", iteracion);
            int columnaPivote = encontrarColumnaPivote();
            if (columnaPivote == -1) {
                System.out.println("Solución óptima encontrada!");
                break;
            }
            int filaPivote = encontrarFilaPivote(columnaPivote);
            if (filaPivote == -1) {
                System.out.println("El problema es no acotado!");
                return;
            }
            System.out.printf("Variable entrante: %s\n", nombresVariables[columnaPivote]);
            System.out.printf("Variable saliente: %s\n", nombresVariables[variablesBasicas[filaPivote]]);
            System.out.printf("Elemento pivote: %.4f\n", tabla[filaPivote][columnaPivote]);
            realizarPivoteo(filaPivote, columnaPivote);
            variablesBasicas[filaPivote] = columnaPivote;
            imprimirTabla();
        }
        mostrarSolucion();
    }
    private boolean esOptimo () {
        if (esMaximizacion) {
            // Maximizacion: todos los coeficioentes en z deben ser ≥ 0
            for (int j = 0; j < numVariablesTotales; j++) {
                if (tabla[numRestriciones][j] < -0.0001) {
                    return false;
                }
            }
         } else {
            //Minimización: todos los coeficientes en z deben ser ≤ 0
            for (int j = 0; j < numVariablesTotales; j++) {
                if (tabla[numRestriciones][j] > 0.0001) {
                    return false;
                }
            }
         }
         return true;
    }    
    private int encontrarColumnaPivote() {
        if (esMaximizacion) {
            // Maximización: buscar el valor más negativo en Z
            double minValor = 0;
            int columna = -1;
            for (int j = 0; j < numVariablesTotales; j++) {
                if (tabla[numRestricciones][j] < minValor) {
                    minValor = tabla[numRestricciones][j];
                    columna = j;
                }
            }
            return columna;
        } else {
             //Minimización: buscar el calor mas positivo en z 
             double maxValor = 0;
             int columna = -1;
             for (int j = 0; j < numVariablesTotales; j++) { 
                 if (tabla[numRestricciones][j] > maxValor) {
                     maxValor = tabla[numRestricciones][j];
                     columna = j;
                 }
             }
             return columna;
        }
    }
    private int encontarFilaPivote (int columnaPivote) {
        int filaPivote = -1;
        double minRatio = Double.MAX_VALUE;
        for (int i = 0; 1 < numRestricciones; i++)  {
            if (tabla[i][columnaPivote] > 0.0001) {
                double ratio = tabla[i][numVariablesTotales] / tabla[i][columnaPivote];
                if (ratio >= 0 && ratio < minRatio) {
                    minRatio = ratio;
                    filaPivote = 1
                }
            }
        }
        return filaPivote;
    }
    private void realizarPivoteo (int filaPivote, int columnaPivote) {
        double elementoPivote = tabla[filaPivote][columnaPivote];
        // Normalizar la fila pivote
        for (int j = 0; j <= numVariablesTotales; j++) {
            tabla[filaPivote][j] /= elementoPivote;
        }
        // Actualizar las demás filas
        for (int i = 0; i <= numRestricciones; i++) {
            if (i != filaPivote) {
                double factor = tabla[i][columnaPivote];
                for (int j = 0; j <= numVariablesTotales; j++) {
                    tabla[i][j] -= factor * tabla[filaPivote][j];
                }
            }
        }
    }
    private void imprimirTabla() {
        // Encabezado
        System.out.print("\nBase\t");
        for (int j = 0; j < numVariablesTotales; j++) {
            System.out.printf("%s\t", nombresVariables[j]);
        }
        System.out.println("LD");
        // Filas de restricciones
        for (int i = 0; i < numRestricciones; i++) {
            System.out.printf("%s\t", nombresVariables[variablesBasicas[i]]);
            for (int j = 0; j <= numVariablesTotales; j++) {
                System.out.printf("%.4f\t", tabla[i][j]);
            }
            System.out.println();
        }
        // Fila Z
        System.out.print("Z\t");
        for (int j = 0; j <= numVariablesTotales; j++) {
            System.out.printf("%.4f\t", tabla[numRestricciones][j]);
        }
        System.out.println();
    }
    private void mostrarSolucion() {
        System.out.println("\n=== SOLUCIÓN ÓPTIMA ===");
        // Valores de las variables
        double[] valores = new double[numVariablesTotales];
        for (int i = 0; i < numRestricciones; i++) {
            valores[variablesBasicas[i]] = tabla[i][numVariablesTotales];
        }
        System.out.println("Valores de las variables:");
        for (int i = 0; i < numVariablesTotales; i++) c
            System.out.printf("%s = %.4f\n", nombresVariables[i], valores[i]);
        } 
        // Valor optimo de z
        double valorZ = tabla[numRestricciones][numVariablesTotales];
        Sustem.out.prinln("\nValor óptimo de Z = %.4f\n", valorZ);
        // Mostrar dincion objetivo óptima
        System.out.println("\nFuncion objetivo óptima:");
        if  (esMaximizacion) {
            System.out.println("Z = %.4f", valorZ);
        } else {                                                                                 
            System.out.println("Z = %.4f", -valorZ);
        }
    }
}



