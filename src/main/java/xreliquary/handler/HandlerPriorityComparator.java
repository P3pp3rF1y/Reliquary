package xreliquary.handler;

import java.util.Comparator;

public class HandlerPriorityComparator implements Comparator<IPrioritizedHandler> {
	@Override
	public int compare(IPrioritizedHandler o1, IPrioritizedHandler o2) {
		int ret = 10 * (o1.getPriority().ordinal() - o2.getPriority().ordinal());
		return ret == 0 ? 1 : ret; //just make every value unique, same priority sorted on the same level
	}
}
