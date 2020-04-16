package number05;

import java.util.ArrayList;
import java.util.List;

public class TF_IDF {
	// TF 계산
	public double calculateTF(String[] terms, String findTerm) {
		double count = 0;
		for (String s : terms) {
			if (s.equals(findTerm)) {
				count++;
			}
		}
		return count / terms.length;
	}

	// IDF 계산
	public double calculateIDF(List<String[]> allTerms, String findTerm) {
		double count = 0;
		for (String[] strings : allTerms) {
			for (String str : strings) {
				if (str.equals(findTerm)) {
					count++;
					break;
				}
			}
		}
		return Math.log(1 + allTerms.size() / count);
	}

	// TF-IDF 계산
	public List<Double> calculateTFIDF(List<String[]> docArrays, String findTerm) {
		List<Double> result = new ArrayList<>();
		double tf;
		double idf;
		for (String[] docTerm : docArrays) {
			tf = calculateTF(docTerm, findTerm);
			idf = calculateIDF(docArrays, findTerm);
			result.add(tf * idf);
		}
		return result;
	}
}
