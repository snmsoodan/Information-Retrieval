

import Jama.Matrix;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Simple Linear Regression implementation
 */
public class linearRegression2 {
	public static void linearRegression() throws Exception {
		Matrix trainingData = MatrixData.getDataMatrix("C:/Users/Snm/Desktop/HW01/train.csv");
		// getMatrix(Initial row index, Final row index, Initial column index, Final column index)
		System.out.println("1");
		Matrix train_x = trainingData.getMatrix(0, trainingData.getRowDimension() - 1, 0, trainingData.getColumnDimension() - 2);
		Matrix train_y = trainingData.getMatrix(0, trainingData.getRowDimension()-1, trainingData.getColumnDimension()-1, trainingData.getColumnDimension()-1);
		System.out.println("2");
		Matrix testData = MatrixData.getDataMatrix("C:/Users/Snm/Desktop/HW01/test.csv");
		Matrix test_x = testData.getMatrix(0, testData.getRowDimension() - 1, 0, testData.getColumnDimension() - 1);
		Matrix test_y = testData.getMatrix(0, testData.getRowDimension() - 1, testData.getColumnDimension() - 1, testData.getColumnDimension() - 1);
		/* Linear Regression */
		/* 2 step process */
		// 1) find beta
		System.out.println("3");
		Matrix beta = getBeta(train_x, train_y);
		System.out.println("3-1");
		Matrix betaStochastic = getBetaStochastic(train_x, train_y);
		System.out.println("3-2");
		Matrix betaBatchGradient = getBetaBatchGradientDescent(train_x, train_y);
		System.out.println("4");
		// 2) predict y for test data using beta calculated from train data
		System.out.println(test_x.getColumnDimension()+" "+test_x.getRowDimension());
		System.out.println(beta.getColumnDimension()+" "+beta.getRowDimension());
		
		
		
		Matrix predictedY = test_x.times(beta);
		Matrix predictedYStochastic = test_x.times(betaStochastic);
		Matrix predictedYBatchGradient = test_x.times(betaBatchGradient);
		System.out.println("5-1");
		double mseCFS = getMeanSqaureError(predictedY, test_y, test_y.getRowDimension());
		double mseStochastic = getMeanSqaureError(predictedYStochastic, test_y, test_y.getRowDimension());
		double mseBatchGradient = getMeanSqaureError(predictedYBatchGradient, test_y, test_y.getRowDimension());
		System.out.println("6");
		// Output
		printOutput(predictedY,"closed_form_linear_regression.txt");
		System.out.println("7");
		printOutput(betaStochastic, "stochastic_gradient_linear_regression.txt");
		printOutput(betaBatchGradient, "batch_gradient_linear_regression.txt");
		System.out.println("Mean Squared Error value for given training data: " + mseCFS);
		System.out.println("Mean Squared Error value for given training data using stochastic method : " + mseStochastic);
		System.out.println("Mean Squared Error value for given training data using Batch Gradient Descent method : " + mseBatchGradient);

		Matrix trainX = getNormalizedMatrix(train_x);
		Matrix testX = getNormalizedMatrix(test_x);
		normalizedcalculation(trainX, testX, train_y, test_y);

	}

	/**  @params: X and Y matrix of training data
	 * returns value of beta calculated using the formula beta = (X^T*X)^ -1)*(X^T*Y)
	 */
	private static Matrix getBeta(Matrix trainX, Matrix trainY) {

		/****************Please Fill Missing Lines Here*****************/

		/*Code addition starts here. Author: Kaushik Veluru*/
		Matrix x = trainX;
		Matrix y = trainY;
		Matrix x_transpose = x.transpose();

		Matrix prod1 = x_transpose.times(x).inverse();
		Matrix prod2 = x_transpose.times(y);

		Matrix beta = prod1.times(prod2);
		return beta;
		/*Code addition ends here. Author: Kaushik Veluru*/
	}

	/*Code addition stars here. Author: Kaushik Veluru*/

	private static double getMeanSqaureError(Matrix predictedY, Matrix testY,int n){
		double mean_square_error = 0.0;
		for (int i =0; i< predictedY.getRowDimension(); i++){
			mean_square_error += Math.pow((predictedY.get(i,0) - testY.get(i,0)), 2);
		}
		return mean_square_error/n;
	}

	private static Matrix getNormalizedMatrix(Matrix x) throws Exception{
		
		//decalre a new normalized matrix that is of same dimensions as the given matrix
		Matrix normalizedX = new Matrix(x.getRowDimension(), x.getColumnDimension());
		DescriptiveStatistics stats = new DescriptiveStatistics();
		List <Double> meanList= new ArrayList<Double>();
		meanList.add(0.0);

		//standard deviation list
		List <Double> sdList= new ArrayList<Double>();
		sdList.add(0.0);
		for (int c=1; c< x.getColumnDimension();c++){
			for (int r=0;r< x.getRowDimension();r++){
				stats.addValue(x.get(r,c));
			}
			
			double mean  = stats.getMean();
			double sd = stats.getStandardDeviation();
			meanList.add(c,mean);
			sdList.add(c, sd);
			stats.clear();
		}

		for (int c=0; c< x.getColumnDimension();c++){
			for (int r=0;r< x.getRowDimension();r++){
				if( c!= 0)
					normalizedX.set(r, c, (x.get(r, c)- meanList.get(c))/sdList.get(c));
				else
					normalizedX.set(r, 0, 1);
			}
		}
		return normalizedX;

	}
	private static void normalizedcalculation(Matrix train_x,Matrix test_x,Matrix train_y,Matrix test_y) throws Exception {
		Matrix beta = getBeta(train_x, train_y);
		Matrix betaStochastic = getBetaStochastic(train_x, train_y);
		Matrix betaBatch = getBetaBatchGradientDescent(train_x, train_y);
		// 2) predict y for test data using beta calculated from train data
		Matrix predictedY = test_x.times(beta);
		Matrix predictedYStochastic = test_x.times(betaStochastic);
		Matrix predictedYBatch = test_x.times(betaBatch);

		double meanSqrError = getMeanSqaureError(predictedY, test_y, test_y.getRowDimension());
		double meanSqrErrorStochastic = getMeanSqaureError(predictedYStochastic, test_y, test_y.getRowDimension());
		double meanSqrErrorBatch = getMeanSqaureError(predictedYBatch, test_y, test_y.getRowDimension());
		// Output
		printOutput(beta, "beta-values-closed-form-zscore-normalized.txt");
		printOutput(betaStochastic, "beta-values-Stochastic-zscore-normalized.txt");
		printOutput(betaBatch, "beta-values-Batch-zscore-normalized.txt");
		printOutput(predictedY,"linear-regression-output-zscore-normalized.txt");
		printOutput(predictedYStochastic,"linear-regression-output-zscore-normalized-Stochastic.txt");
		printOutput(predictedYBatch,"linear-regression-output-zscore-normalized-Batch.txt");
		System.out.println("Normalized Mean Square Error:" + meanSqrError);
		System.out.println("Normalized Mean Square Error for Stochastic:" + meanSqrErrorStochastic);
		System.out.println("Normalized Mean Square Error for Batch:" + meanSqrErrorBatch);
	}

	public static Matrix getBetaBatchGradientDescent(Matrix trainX, Matrix trainY) throws IOException
	{
		System.out.println("column "+trainX.getColumnDimension());
		System.out.println("row "+trainX.getRowDimension());
		int counter = 0;
		Matrix beta = new Matrix(trainX.getColumnDimension(), 1, 0.6);
		Matrix betaT = new Matrix(trainX.getColumnDimension(), 1, 0);
//		Matrix intermediateMat =  trainX.getMatrix(0, 999, 0, 99);
		Matrix intermediateMat =  trainX.getMatrix(0, trainX.getRowDimension()-1, 0, 4);
		Matrix intermediateMat2 = new Matrix(trainX.getColumnDimension(), 1, 0.0);
		Matrix errorMat = new Matrix(trainX.getColumnDimension(), 1, 0.0);
		double eta = 0.01;
		boolean convergence = true;

		while(convergence)
		{
			for(int i=0;i<trainX.getRowDimension();i++)
			{   
				intermediateMat = (trainX.getMatrix(i, i, 0, trainX.getColumnDimension()-1)).transpose();
				intermediateMat2 = (trainX.getMatrix(i, i, 0, trainX.getColumnDimension()-1)).transpose();
				Matrix xt = intermediateMat.transpose();
				intermediateMat = xt.times(beta);
				double val = intermediateMat.get(0, 0);
				val = val - trainY.get(i, 0);
				intermediateMat2 = intermediateMat2.timesEquals(2.0);
				intermediateMat2 = intermediateMat2.times(val);
				errorMat = errorMat.plus(intermediateMat2);
			}

			double rowInverse = 1.0/trainX.getRowDimension();
			errorMat = errorMat.times(rowInverse);
			beta = beta.minus(errorMat.times(eta));

			double oldBetaNorm = beta.norm2();
			double newBetaNorm = betaT.norm2();

			if(Math.abs(newBetaNorm - oldBetaNorm) <= 0.0001 || counter > 2000)		
				convergence = false;

			betaT = beta;
			counter++;
		}
		return beta;
	}

	public static Matrix getBetaStochastic(Matrix trainX, Matrix trainY) throws IOException
	{
//		System.out.println(trainX.getRowDimension());
		int temp = 0;
		Matrix beta = new Matrix(trainX.getColumnDimension(), 1, 0.5);
		Matrix betaT = new Matrix(trainX.getColumnDimension(), 1, 0.4);
		Matrix intermediateMat = new Matrix(trainX.getColumnDimension(), 1, 0.0);
		Matrix intermediateMat2 = new Matrix(trainX.getColumnDimension(), 1, 0.0);
		double eta = 0.001;
		boolean convergence = true;
		double trainVal = trainY.normF();
		double value = 0.0;
		while(convergence)
		{
			for(int i=0;i<trainX.getRowDimension();i++)
			{
				intermediateMat = (trainX.getMatrix(i, i, 0, trainX.getColumnDimension()-1)).transpose();
				intermediateMat2 = intermediateMat.transpose();
				intermediateMat2 = intermediateMat2.times(beta);
				value = trainY.get(i,0) - intermediateMat2.get(0, 0);
				value = value * eta * 2.0 ;
				intermediateMat = intermediateMat.times(value);

//				if(intermediateMat.norm2() >= trainY.get(i, 0))
//					continue;
				beta = beta.plus(intermediateMat);
				double oldBetaNorm = beta.norm2();
				double newBetaNorm = betaT.norm2();

				if((Math.abs(newBetaNorm - oldBetaNorm)<= 0.0001)||(temp > 2000))
				{
					convergence = false;
					break;
				}

			}
			betaT = beta;
//			System.out.println(temp);
			temp++;
		}
		return beta;
	}
	/*Code addition ends here. Author : Kaushik Veluru*/

	/**
	 * @params: predicted Y matrix
	 * outputs the predicted y values to the text file named "linear-regression-output"
	 */
	public static void printOutput(Matrix predictedY,String output_file) throws IOException {
		FileWriter fStream = new FileWriter("C:/Users/Snm/Desktop/HW6/output/"+output_file);     // Output File
		BufferedWriter out = new BufferedWriter(fStream);
		for (int row =0; row<predictedY.getRowDimension(); row++) {
//			System.out.println("predict "+predictedY.getColumnDimension());
			out.write(String.valueOf(predictedY.get(row, 0)));
			out.newLine();
		}
		out.close();
	}
}
