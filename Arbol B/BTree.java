public class BTree<E extends Comparable<? super E>> {
	
	BNodeGeneric<E> root;
	int MinDeg;

    public boolean add(E value) {
    	
    	if(search(value) == true){
    		return false;
    	}
    	else {
    		if (root == null){
    	
            root = new BNodeGeneric<E>(MinDeg,true);
            root.keys.set(0, value);
            root.num = 1;
            
        }
        else {
            if (root.num == 2*MinDeg-1){
            	BNodeGeneric<E> s = new BNodeGeneric<E>(MinDeg,true);
                s.children.set(0, root);
                s.splitChild(0,root);

                int i = 0;
                if (s.keys.get(i).compareTo(value) < 0)
                    i++;
                s.children.get(i).insertNotFull(value);
                root = s;
            }
             else
                root.insertNotFull(value);
        }
    	}
    	
    	return true;

    }

    public E remove(E value) {
    	if (root == null){
            System.out.println("The tree is empty");
            return null;
        }

        root.remove(value);

        if (root.num == 0){
            if (root.isLeaf)
                root = null;
            else
                root = root.children.get(0);
        }
        
        return value;
    }

    public void clear() {
        root = null;
    }

    public boolean search(E value) {
        
    	if(root.search(value) == null) {
    		return false;
    	}
    	
    	return true;
    }

    public int size() {
    	return size(root);
    }
    
    public int size (BNodeGeneric<E> n) {
    	return n.num;
    }
}
