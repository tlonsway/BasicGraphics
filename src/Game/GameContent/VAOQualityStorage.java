package Game.GameContent;

import Game.Graphics.*;

public class VAOQualityStorage {
	VAOStorage vaoHQ;
	VAOStorage vaoMQ;
	VAOStorage vaoLQ;
	
	public VAOQualityStorage(Mesh mHQ, Mesh mMQ, Mesh mLQ) {
		vaoHQ = new VAOStorage(mHQ);
		vaoMQ = new VAOStorage(mMQ);
		vaoLQ = new VAOStorage(mLQ);
	}
	
	public VAOQualityStorage(VAOStorage HQ, VAOStorage MQ, VAOStorage LQ) {
		this.vaoHQ = HQ;
		this.vaoMQ = MQ;
		this.vaoLQ = LQ;
	}
	
	public VAOStorage getVAOHQ() {
		return vaoHQ;
	}
	public VAOStorage getVAOMQ() {
		return vaoMQ;
	}
	public VAOStorage getVAOLQ() {
		return vaoLQ;
	}
	
}
