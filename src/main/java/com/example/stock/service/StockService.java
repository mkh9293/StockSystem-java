package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * 1. synchronized 를 사용해서 동시성 문제 해결
     * @Transactional 어노테이션이 붙으면 아래와 같이 메소드가 작성되는데 endTransaction 에 도달하기전에
     * 다른 트랜잭션이 시작되는 경우에 정합성 문제가 발생한다.
     *
     * startTransaction
     *
     * decrease()
     *
     * endTransaction
     *
     * ex.
     * 1. startTranscation t1 시작 (t1-100)
     * 2. startTranscation t2 시작 (t2-100)
     * 3. t1 이 decrease() 먼저 락을 획득하고 잠금 (t1-100)
     * 4. t1 이 잠금을 해제하고 나옴 (아직 endTransaction 까지 도달하지 않은 상태) (t1-99)
     * 5. t2 가 decrease() 락을 획득하고 잠금 후 작업 완료하여 해제 (t2-99)
     * 6. t1 가 endTransaction t1-99 를 실제 디비에 flush
     * 7. t2 가 endTransaction t2-99 를 실제 디비에 flush
     * 98 개의 재고 수를 예상했지만 레이스 컨디션에 의해 99 개로 남는 문제 발생
     */
//    public synchronized void decrease(Long id, Long quantity) {
//        Stock stock = stockRepository.findById(id).orElseThrow();
//        stock.decrease(quantity);
//        stockRepository.saveAndFlush(stock);
//    }

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
    }

    // 첫번째 예시
//    @Transactional
//    public void decrease(Long id, Long quantity) {
//        Stock stock = stockRepository.findById(id).orElseThrow();
//        stock.decrease(quantity);
//    }

}
