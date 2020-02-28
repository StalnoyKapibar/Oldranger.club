package ru.java.mentor.oldranger.club.service.article.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleTagsNodeRepository;
import ru.java.mentor.oldranger.club.dto.ArticleTagsNodeDto;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;
import ru.java.mentor.oldranger.club.service.article.ArticleTagsNodeService;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
@CacheConfig(cacheNames = {"tagNode"})
public class ArticleTagsNodeServiceImp implements ArticleTagsNodeService {
    private ArticleTagsNodeRepository tagsNodeRepository;

    @Override
    @Cacheable(cacheNames = {"allTagNode"})
    public List<ArticleTagsNode> findAll() {
        log.info("Getting list of all nodes");
        List<ArticleTagsNode> tagsNodes = null;
        try {
            tagsNodes = tagsNodeRepository.findAll();
            log.debug("List returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return tagsNodes;
    }

    @Override
    @Cacheable(cacheNames = {"allTagNodeHierarchy"}, keyGenerator = "customKeyGenerator")
    public List<ArticleTagsNodeDto> findHierarchyTreeOfAllTagsNodes() {
        log.info("Getting list of all nodes DTO");
        List<ArticleTagsNodeDto> tagsNodeDto = null;
        try {
            tagsNodeDto = tagsNodeRepository.findHierarchyTreeOfAllTagsNodes();
            log.debug("List of all DTO returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return tagsNodeDto;
    }

    @Override
    @Cacheable
    public ArticleTagsNode findById(Long id) {
        log.info("Getting node by id = {}", id);
        ArticleTagsNode tagsNode = null;
        try {
            tagsNode = tagsNodeRepository.findById(id).orElse(null);
            log.debug("Returned node with id = {}", id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return tagsNode;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "tagNode", allEntries = true),
            @CacheEvict(value = "allTagNode", allEntries = true)
            })
    public void save(ArticleTagsNode tagsNode) {
        log.info("Saving node {} or editing node by id = {}", tagsNode, tagsNode.getId());
        try {
            tagsNodeRepository.save(tagsNode);
            log.debug("Node with id = {} saved or updated", tagsNode.getId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "tagNode", allEntries = true),
            @CacheEvict(value = "allTagNode", allEntries = true)})
    public void deleteById(Long id) {
        log.info("Deleting node with id = {}", id);
        try {
            tagsNodeRepository.deleteByIdIn(tagsNodeRepository.findDescendantsAndParentIdsByParentId(id));
            log.debug("Node {} deleted", id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
