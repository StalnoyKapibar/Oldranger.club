package ru.java.mentor.oldranger.club.service.article.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.java.mentor.oldranger.club.dao.ArticleRepository.ArticleTagsNodeRepository;
import ru.java.mentor.oldranger.club.dto.ArticleTagsNodeDto;
import ru.java.mentor.oldranger.club.model.article.ArticleTagsNode;
import ru.java.mentor.oldranger.club.service.article.ArticleTagsNodeService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class ArticleTagsNodeServiceImp implements ArticleTagsNodeService {
    private ArticleTagsNodeRepository tagsNodeRepository;

    @Override
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
    public List<ArticleTagsNodeDto> findHierarchyTreeOfAllTagsNodes() {
        log.info("Getting list of all nodes DTO");
        List<ArticleTagsNodeDto> tagsNodeDto = null;
        try {
            tagsNodeDto = tagsNodeRepository.findAllChildrenTree().stream()
                    .map(e -> new ArticleTagsNodeDto(
                            Long.valueOf(e.get("id").toString()),
                            e.get("parent") == null ? null :  Long.valueOf(e.get("parent").toString()),
                            e.get("tag_name", String.class),
                            Arrays.stream(e.get("tags_hierarchy", String.class).split(",")).mapToInt(Integer::parseInt).toArray())).collect(Collectors.toList());
            log.debug("List of all DTO returned");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return tagsNodeDto;
    }

    @Override
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
