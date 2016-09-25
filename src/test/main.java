package test;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		int doRok = 1000;
		
		
		double vklad = 0.9f;
		for(int rok = 1; rok < doRok ; rok++){
			for(int mesic =1 ; mesic < 12; mesic++){
				vklad = vklad + (vklad * 0.024f);
			}
		}
	vklad = Math.round(vklad);
	System.out.println("Celková zúroèená èástka za "+ doRok + " rokù, je rovna " + vklad + " USD");
	System.out.printf("%.0f",vklad);
	}

}
