import java.util.Scanner;
public class Simples{
    //Atributos 

    private double [] tabla; //Tabla simples
    private int numVariables; //Numero de variables
    private int numRestricciones; //Numero de restricciones
    private int numVariablesTotales; // Variables mas los de holgura 
    private boolean esMaximizacion;
    private int variablesBasicas;
    private String [] nombresVariables;
    public static void main(String [] args){

        Scanner scanner = new Scanner(System.in);
        System.out.println("=== MÉTODO SIMPLEX ===");// Titulo
        System.out.println("¿Es maximización (1) o minimización (2)?: ");//Seleccion de operacion a realizar
        int tipo = scanner.nextInt();
        boolean esMaximizacion = (tipo == 1);
        System.out.println("Número de variables de decisión (x1, x2, ...): ");//Numero de varaibles
        int numVariables = scanner.nextInt();
        System.out.println("Numero de restricciones: ");//cantidad de restricciones
        int numRestricciones = scanner.nextInt();
        Simplex simplex = new Simplex(numVariables, numRestricciones, esMaximizacion);
        simplex.ingresaDatos(scanner);
        simplex.resolver();
        simplex.close();
    }
    public Simplex(int numVariables, int numRestricciones, boolean esMaximizacion){
        this.numVariables = NumVariables;
        this.numRestriciones = numRestricciones;
        this.esMaximizacion = esMaximizacion;
        this.numVariablesTotales = numVariables + numRestricciones;
        this.variablesBasicas = new [numRestricciones];
        this.nombresVariables = new String[numVariablesTotales];
        //Iniciar nombre de variables
        for(int i=0; i<numVariables; i++){
            nombresVariables[i] = "x " + (i+1);
        }
        for(int i=0; i<numRestricciones; i++){
            nombresVariables[numVariables + i] = "s" + (i+1);
        }
        // Inicializar tabla simplex
        tabla = new double[numRestricciones + 1][numVariablesTotales + 1];
    }
    // se crea el metodo para ingresar los datos
    public void ingresaDatos(Scanner Scanner){
        System.out.prontln("\n===INGRESO DE LA FUNCION OBGETIVO===");
        System.out.println("Ingrese los coeficientes de la duncion objetivo:");
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
        // Coeficientes de las variables de holgura en la función objetivo = 1
        for (int j = numVariables; j < numVariablesTotales; j++) {
            tabla[numRestricciones][j] = 1.0;
        }
        System.out.println("\n=== INGRESO DE RESTRICCIONES ===");
        for (int i = 0; i < numRestricciones; i++) {
            System.out.printf("\n--- Restricción %d ---\n", i + 1);
            // Coeficientes de las variables de decisión
            for (int j = 0; j < numVariables; j++) {
                System.out.printf("Coeficiente de x%d: ", j + 1);
                tabla[i][j] = scanner.nextDouble();
            }
            //Coeficientes de las variables de holgura
            for (int j = numVariables; j < numVariablesTotales; j++){
                tabla[i][j] = (j == numVariables + i) ? 1.0: 0.0;
            }
            //Lado derecho (LD)
            System.out.println("Lado derecho (LD) de la restricción:");
            tabla[i][numVariablesTotales] = scanner.nextDouble();
            variablesBasicas[i] = numVariables + i; //Variable de holgura básica inicial 
        }
        // Mostrar tabla inicial
        System.out.println("\n=== TABLA INICIAL SIMPLEX ===");
        imprimirTabla();
    }
    //se crea el metodo resolver
    public void resolver(){
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
    //Se verifica si es optimo
    private boolean esOptimo(){
        if (esMaximizacion){
          //Maximizaci+on: todos los coeficientes en z deben ser ≥ 0
            for(int j = 0; j < numVariablesTotales; j++){
                if (tabla[numRestricciones][j] < -0.0001){
                    return false;
                }
            }
        } else {
            // Minimización: todos los coeficientes en Z deben ser ≤ 0
            for (int j = 0; j < numVariablesTotales; j++) {
                if (tabla[numRestricciones][j] > 0.0001) {
                    return false;
                }
            }
        }
        return true;
    }
    //se encuntra la columna pivote
    private int encontrarColumnaPivote(){
        if(esMaximizacion){
            //Maximización: buscar el valor mas negativo en z 
            double minValor = 0;
            int columna = -1;
            for(int j = 0; j < numVariablesTotales; j++){
                if(tabla[numRestricciones][j] < minValor){
                    minValor = tabla[numRestricciones][j];
                    columna = j;
                }
            }
            return columna;
        } else {
            // Minimización: buscar el valor más positivo en Z
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
    //encontrar la fila pivote
    private int encontrarFilaPivote(int columnaPivote){
        int filaPivote = -1;
        double minRatio = Double.MAX_VALUE;
        for (int i = 0; i < numRestricciones; i++) {
            if (tabla[i][columnaPivote] > 0.0001) {
                double ratio = tabla[i][numVariablesTotales] / tabla[i][columnaPivote];
                if (ratio >= 0 && ratio < minRatio) {
                    minRatio = ratio;
                    filaPivote = i;
                }
            }
        }
        return filaPivote;
    }
    //realizar las operaciones correspondientes
    private void realizarPivoteo(int filaPivote, int columnaPivote){
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
    //Imprimir la tabla
    private void imprimirTabla(){
        //Encabezado
        System.out.println("\nBase\t");
        for(int j = 0; j < numVariablesTotales; j++){
            System.out.println("%s\t", nombresVariables[j]);
        }
        System.out.println("LD");
        //Filas de restricciones
        for(int i = 0; i < numRestriciones; i++){
            System.out.println("%s\t", nombresVaiables[variablesBasicas[i]]);
            for(int j = 0; j < = numVariablesTotales; j++){
                System.out.println("%.4f\t", tabla[i][j]);
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
    //mostrar la solucion
    private void mostrarSolucion(){
        System.out.println("\n=== SOLUCIÓN ÓPTIMA ===");
        // Valores de las variables
        double[] valores = new double[numVariablesTotales];
        for (int i = 0; i < numRestricciones; i++) {
            valores[variablesBasicas[i]] = tabla[i][numVariablesTotales];
        }
        System.out.println("Valores de las variables:");
        for (int i = 0; i < numVariablesTotales; i++) {
            System.out.printf("%s = %.4f\n", nombresVariables[i], valores[i]);
        } 
        // Valor óptimo de Z
        double valorZ = tabla[numRestricciones][numVariablesTotales];
        System.out.printf("\nValor óptimo de Z = %.4f\n", valorZ);
        // Mostrar función objetivo óptima
        System.out.println("\nFunciÓn objetivo óptima:");
        if (esMaximizacion) {
            System.out.printf("Z = %.4f", valorZ);
        } else {
            System.out.printf("Z = %.4f", -valorZ);
        }
    }
}

